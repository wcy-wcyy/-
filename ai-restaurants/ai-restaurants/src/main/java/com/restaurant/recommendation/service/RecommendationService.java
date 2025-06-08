package com.restaurant.recommendation.service;

import com.restaurant.recommendation.dto.RecommendationRequest;
import com.restaurant.recommendation.entity.Dish;
import com.restaurant.recommendation.entity.Recommendation;
import com.restaurant.recommendation.entity.User;
import com.restaurant.recommendation.repository.DishRepository;
import com.restaurant.recommendation.repository.RecommendationRepository;
import com.restaurant.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DishRepository dishRepository;
    
    @Autowired
    private RecommendationRepository recommendationRepository;
    
    /**
     * 为用户生成个性化推荐
     */
    public List<Recommendation> generateRecommendations(RecommendationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 获取所有候选菜品
        List<Dish> allDishes = dishRepository.findAll();
        
        // 过滤不适合的菜品
        List<Dish> candidateDishes = filterDishes(allDishes, user, request);
        
        // 计算推荐得分并排序
        List<Recommendation> recommendations = candidateDishes.stream()
                .map(dish -> calculateRecommendationScore(user, dish, request))
                .sorted((r1, r2) -> r2.getScore().compareTo(r1.getScore()))
                .limit(request.getCount())
                .collect(Collectors.toList());
        
        // 保存推荐记录
        return recommendationRepository.saveAll(recommendations);
    }
    
    /**
     * 过滤不适合的菜品
     */
    private List<Dish> filterDishes(List<Dish> dishes, User user, RecommendationRequest request) {
        return dishes.stream()
                .filter(dish -> !hasAllergenConflict(dish, user, request))
                .filter(dish -> !hasDietaryRestrictionConflict(dish, user, request))
                .filter(dish -> !hasHealthConflict(dish, user))
                .filter(dish -> matchesNutritionRequirements(dish, request))
                .collect(Collectors.toList());
    }
    
    /**
     * 检查过敏原冲突
     */
    private boolean hasAllergenConflict(Dish dish, User user, RecommendationRequest request) {
        Set<String> userAllergies = new HashSet<>();
        if (user.getAllergies() != null) {
            userAllergies.addAll(user.getAllergies());
        }
        if (request.getAllergies() != null) {
            userAllergies.addAll(request.getAllergies());
        }
        
        if (dish.getAllergens() != null) {
            return dish.getAllergens().stream().anyMatch(userAllergies::contains);
        }
        return false;
    }
    
    /**
     * 检查饮食限制冲突
     */
    private boolean hasDietaryRestrictionConflict(Dish dish, User user, RecommendationRequest request) {
        Set<String> restrictions = new HashSet<>();
        if (user.getDietaryRestrictions() != null) {
            restrictions.addAll(user.getDietaryRestrictions());
        }
        if (request.getDietaryRestrictions() != null) {
            restrictions.addAll(request.getDietaryRestrictions());
        }
        
        // 简单的冲突检查逻辑
        if (restrictions.contains("素食") && dish.getIngredients() != null) {
            return dish.getIngredients().stream()
                    .anyMatch(ingredient -> ingredient.contains("肉") || ingredient.contains("鱼"));
        }
        
        return false;
    }
    
    /**
     * 检查健康状况冲突
     */
    private boolean hasHealthConflict(Dish dish, User user) {
        if (user.getDiseases() != null && dish.getHealthWarnings() != null) {
            return user.getDiseases().stream()
                    .anyMatch(disease -> dish.getHealthWarnings().contains(disease));
        }
        return false;
    }
    
    /**
     * 检查营养需求匹配
     */
    private boolean matchesNutritionRequirements(Dish dish, RecommendationRequest request) {
        if (request.getMaxCalories() != null && dish.getCalories() != null) {
            if (dish.getCalories() > request.getMaxCalories()) {
                return false;
            }
        }
        
        if (request.getMinProtein() != null && dish.getProtein() != null) {
            if (dish.getProtein() < request.getMinProtein()) {
                return false;
            }
        }
        
        if (request.getMaxFat() != null && dish.getFat() != null) {
            if (dish.getFat() > request.getMaxFat()) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 计算推荐得分
     */
    private Recommendation calculateRecommendationScore(User user, Dish dish, RecommendationRequest request) {
        Recommendation recommendation = new Recommendation(user, dish, 0.0, "");
        
        double totalScore = 0.0;
        StringBuilder reason = new StringBuilder();
        
        // 1. 菜系偏好匹配 (权重: 25%)
        double cuisineScore = calculateCuisineMatch(user, dish, request);
        recommendation.setCuisineMatch(cuisineScore);
        totalScore += cuisineScore * 0.25;
        if (cuisineScore > 0.7) {
            reason.append("符合您喜爱的").append(dish.getCuisine()).append("菜系; ");
        }
        
        // 2. 口味偏好匹配 (权重: 20%)
        double flavorScore = calculateFlavorMatch(user, dish, request);
        recommendation.setFlavorMatch(flavorScore);
        totalScore += flavorScore * 0.20;
        if (flavorScore > 0.7) {
            reason.append("口味").append(dish.getFlavor()).append("符合偏好; ");
        }
        
        // 3. 食材偏好匹配 (权重: 15%)
        double ingredientScore = calculateIngredientMatch(user, dish, request);
        recommendation.setIngredientMatch(ingredientScore);
        totalScore += ingredientScore * 0.15;
        
        // 4. 健康匹配度 (权重: 25%)
        double healthScore = calculateHealthMatch(user, dish);
        recommendation.setHealthMatch(healthScore);
        totalScore += healthScore * 0.25;
        if (healthScore > 0.8) {
            reason.append("有益健康; ");
        }
        
        // 5. 营养匹配度 (权重: 15%)
        double nutritionScore = calculateNutritionMatch(user, dish);
        recommendation.setNutritionMatch(nutritionScore);
        totalScore += nutritionScore * 0.15;
        
        // 转换为百分制
        recommendation.setScore(totalScore * 100);
        
        if (reason.length() == 0) {
            reason.append("综合推荐");
        }
        recommendation.setReason(reason.toString());
        
        return recommendation;
    }
    
    /**
     * 计算菜系匹配度
     */
    private double calculateCuisineMatch(User user, Dish dish, RecommendationRequest request) {
        Set<String> preferredCuisines = new HashSet<>();
        if (user.getPreferredCuisines() != null) {
            preferredCuisines.addAll(user.getPreferredCuisines());
        }
        if (request.getPreferredCuisines() != null) {
            preferredCuisines.addAll(request.getPreferredCuisines());
        }
        
        if (preferredCuisines.isEmpty()) return 0.5; // 中性分数
        
        return preferredCuisines.contains(dish.getCuisine()) ? 1.0 : 0.3;
    }
    
    /**
     * 计算口味匹配度
     */
    private double calculateFlavorMatch(User user, Dish dish, RecommendationRequest request) {
        Set<String> preferredFlavors = new HashSet<>();
        if (user.getPreferredFlavors() != null) {
            preferredFlavors.addAll(user.getPreferredFlavors());
        }
        if (request.getPreferredFlavors() != null) {
            preferredFlavors.addAll(request.getPreferredFlavors());
        }
        
        if (preferredFlavors.isEmpty()) return 0.5;
        
        return preferredFlavors.contains(dish.getFlavor()) ? 1.0 : 0.3;
    }
    
    /**
     * 计算食材匹配度
     */
    private double calculateIngredientMatch(User user, Dish dish, RecommendationRequest request) {
        Set<String> preferredIngredients = new HashSet<>();
        if (user.getPreferredIngredients() != null) {
            preferredIngredients.addAll(user.getPreferredIngredients());
        }
        if (request.getPreferredIngredients() != null) {
            preferredIngredients.addAll(request.getPreferredIngredients());
        }
        
        if (preferredIngredients.isEmpty() || dish.getIngredients() == null) {
            return 0.5;
        }
        
        long matchCount = dish.getIngredients().stream()
                .mapToLong(ingredient -> preferredIngredients.stream()
                        .mapToLong(preferred -> ingredient.contains(preferred) ? 1 : 0)
                        .sum())
                .sum();
        
        return Math.min(1.0, (double) matchCount / preferredIngredients.size());
    }
    
    /**
     * 计算健康匹配度
     */
    private double calculateHealthMatch(User user, Dish dish) {
        double score = 0.5; // 基础分数
        
        // 如果有疾病，检查是否有益
        if (user.getDiseases() != null && dish.getHealthBenefits() != null) {
            long benefitCount = user.getDiseases().stream()
                    .mapToLong(disease -> dish.getHealthBenefits().contains(disease) ? 1 : 0)
                    .sum();
            if (benefitCount > 0) {
                score += 0.3;
            }
        }
        
        // 根据BMI调整分数
        Double bmi = user.getBMI();
        if (bmi != null && dish.getCalories() != null) {
            if (bmi > 25 && dish.getCalories() < 150) { // 超重用户推荐低热量
                score += 0.2;
            } else if (bmi < 18.5 && dish.getCalories() > 200) { // 偏瘦用户推荐高热量
                score += 0.2;
            }
        }
        
        return Math.min(1.0, score);
    }
    
    /**
     * 计算营养匹配度
     */
    private double calculateNutritionMatch(User user, Dish dish) {
        if (dish.getCalories() == null) return 0.5;
        
        Double bmr = user.getBMR();
        if (bmr == null) return 0.5;
        
        // 根据基础代谢率评估营养匹配度
        double dailyCalorieNeed = bmr * 1.5; // 简化的日常热量需求
        double mealCalorieTarget = dailyCalorieNeed / 3; // 假设一餐的目标热量
        
        double calorieRatio = dish.getCalories() / mealCalorieTarget;
        
        // 理想范围是0.8-1.2倍目标热量
        if (calorieRatio >= 0.8 && calorieRatio <= 1.2) {
            return 1.0;
        } else if (calorieRatio >= 0.6 && calorieRatio <= 1.5) {
            return 0.7;
        } else {
            return 0.3;
        }
    }
    
    /**
     * 获取用户的推荐历史
     */
    public List<Recommendation> getUserRecommendationHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return recommendationRepository.findByUserOrderByRecommendedAtDesc(user);
    }
    
    /**
     * 更新推荐反馈
     */
    public void updateRecommendationFeedback(Long recommendationId, boolean clicked, 
                                           boolean ordered, Integer rating, String feedback) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new RuntimeException("推荐记录不存在"));
        
        recommendation.setIsClicked(clicked);
        recommendation.setIsOrdered(ordered);
        recommendation.setUserRating(rating);
        recommendation.setUserFeedback(feedback);
        
        recommendationRepository.save(recommendation);
    }
} 