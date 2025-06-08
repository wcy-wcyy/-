package com.restaurant.recommendation.service;

import com.restaurant.recommendation.entity.Dish;
import com.restaurant.recommendation.entity.NutritionReport;
import com.restaurant.recommendation.entity.User;
import com.restaurant.recommendation.repository.NutritionReportRepository;
import com.restaurant.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NutritionAnalysisService {
    
    @Autowired
    private NutritionReportRepository nutritionReportRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 为用户生成营养分析报告
     */
    public NutritionReport generateNutritionReport(Long userId, List<Dish> consumedDishes, 
                                                  LocalDateTime reportDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 检查当日是否已有报告
        Optional<NutritionReport> existingReport = nutritionReportRepository
                .findByUserAndReportDate(user, reportDate);
        
        NutritionReport report;
        if (existingReport.isPresent()) {
            report = existingReport.get();
        } else {
            report = new NutritionReport(user, reportDate);
        }
        
        // 计算营养摄入汇总
        calculateNutritionSummary(report, consumedDishes);
        
        // 计算推荐摄入量
        calculateRecommendedIntake(report, user);
        
        // 计算营养比例
        calculateNutritionPercentages(report);
        
        // 生成健康评估
        generateHealthAssessment(report, user);
        
        // 生成营养建议
        generateNutritionRecommendations(report, user);
        
        // 设置包含的菜品列表
        List<String> dishNames = consumedDishes.stream()
                .map(Dish::getName)
                .collect(java.util.stream.Collectors.toList());
        report.setIncludedDishes(dishNames);
        
        return nutritionReportRepository.save(report);
    }
    
    /**
     * 计算营养摄入汇总
     */
    private void calculateNutritionSummary(NutritionReport report, List<Dish> dishes) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbohydrate = 0;
        double totalFiber = 0;
        double totalSodium = 0;
        double totalCholesterol = 0;
        
        for (Dish dish : dishes) {
            if (dish.getCalories() != null) totalCalories += dish.getCalories();
            if (dish.getProtein() != null) totalProtein += dish.getProtein();
            if (dish.getFat() != null) totalFat += dish.getFat();
            if (dish.getCarbohydrate() != null) totalCarbohydrate += dish.getCarbohydrate();
            if (dish.getFiber() != null) totalFiber += dish.getFiber();
            if (dish.getSodium() != null) totalSodium += dish.getSodium();
            if (dish.getCholesterol() != null) totalCholesterol += dish.getCholesterol();
        }
        
        report.setTotalCalories(totalCalories);
        report.setTotalProtein(totalProtein);
        report.setTotalFat(totalFat);
        report.setTotalCarbohydrate(totalCarbohydrate);
        report.setTotalFiber(totalFiber);
        report.setTotalSodium(totalSodium);
        report.setTotalCholesterol(totalCholesterol);
    }
    
    /**
     * 计算推荐摄入量
     */
    private void calculateRecommendedIntake(NutritionReport report, User user) {
        Double bmr = user.getBMR();
        if (bmr == null) {
            // 使用默认值
            report.setRecommendedCalories(2000.0);
            report.setRecommendedProtein(60.0);
            report.setRecommendedFat(65.0);
            report.setRecommendedCarbohydrate(300.0);
        } else {
            // 根据基础代谢率和活动水平计算
            double activityFactor = 1.5; // 轻度活动
            double dailyCalories = bmr * activityFactor;
            
            report.setRecommendedCalories(dailyCalories);
            report.setRecommendedProtein(dailyCalories * 0.15 / 4); // 15%热量来自蛋白质，每克4卡
            report.setRecommendedFat(dailyCalories * 0.30 / 9);     // 30%热量来自脂肪，每克9卡
            report.setRecommendedCarbohydrate(dailyCalories * 0.55 / 4); // 55%热量来自碳水，每克4卡
        }
    }
    
    /**
     * 计算营养比例
     */
    private void calculateNutritionPercentages(NutritionReport report) {
        Double totalCalories = report.getTotalCalories();
        if (totalCalories == null || totalCalories == 0) {
            report.setProteinPercentage(0.0);
            report.setFatPercentage(0.0);
            report.setCarbohydratePercentage(0.0);
            return;
        }
        
        Double protein = report.getTotalProtein();
        Double fat = report.getTotalFat();
        Double carb = report.getTotalCarbohydrate();
        
        if (protein != null) {
            report.setProteinPercentage((protein * 4 / totalCalories) * 100);
        }
        if (fat != null) {
            report.setFatPercentage((fat * 9 / totalCalories) * 100);
        }
        if (carb != null) {
            report.setCarbohydratePercentage((carb * 4 / totalCalories) * 100);
        }
    }
    
    /**
     * 生成健康评估
     */
    private void generateHealthAssessment(NutritionReport report, User user) {
        int healthScore = 100;
        StringBuilder warnings = new StringBuilder();
        
        // 检查热量摄入
        if (report.isCaloriesExceeded()) {
            healthScore -= 20;
            warnings.append("热量摄入超标，建议减少高热量食物；");
        }
        
        // 检查蛋白质摄入
        if (!report.isProteinAdequate()) {
            healthScore -= 15;
            warnings.append("蛋白质摄入不足，建议增加蛋白质丰富的食物；");
        }
        
        // 检查钠摄入
        if (report.getTotalSodium() != null && report.getTotalSodium() > 2300) {
            healthScore -= 15;
            warnings.append("钠摄入过高，建议减少盐分摄入；");
        }
        
        // 检查营养平衡
        Double balance = report.getNutritionBalance();
        if (balance != null && balance < 70) {
            healthScore -= 10;
            warnings.append("营养比例不均衡，建议调整饮食结构；");
        }
        
        // 检查用户特殊健康状况
        if (user.getDiseases() != null) {
            for (String disease : user.getDiseases()) {
                if ("糖尿病".equals(disease) && report.getTotalCarbohydrate() != null && 
                    report.getTotalCarbohydrate() > report.getRecommendedCarbohydrate() * 1.2) {
                    healthScore -= 25;
                    warnings.append("碳水化合物摄入过多，不利于血糖控制；");
                }
                if ("高血压".equals(disease) && report.getTotalSodium() != null && 
                    report.getTotalSodium() > 1500) {
                    healthScore -= 25;
                    warnings.append("钠摄入过高，不利于血压控制；");
                }
            }
        }
        
        // 设置健康状态
        if (healthScore >= 80) {
            report.setHealthStatus("健康");
        } else if (healthScore >= 60) {
            report.setHealthStatus("需改善");
        } else {
            report.setHealthStatus("不健康");
        }
        
        report.setHealthScore(Math.max(0, healthScore));
        report.setWarnings(warnings.toString());
    }
    
    /**
     * 生成营养建议
     */
    private void generateNutritionRecommendations(NutritionReport report, User user) {
        StringBuilder recommendations = new StringBuilder();
        
        // 基于热量摄入的建议
        if (report.isCaloriesExceeded()) {
            recommendations.append("建议：1.选择低热量、高纤维的食物；2.控制食物分量；3.增加运动量。");
        } else if (report.getTotalCalories() != null && report.getRecommendedCalories() != null &&
                   report.getTotalCalories() < report.getRecommendedCalories() * 0.8) {
            recommendations.append("建议：1.适当增加食物摄入量；2.选择营养密度高的食物；3.少食多餐。");
        }
        
        // 基于蛋白质摄入的建议
        if (!report.isProteinAdequate()) {
            recommendations.append("蛋白质建议：1.增加瘦肉、鱼类、豆类摄入；2.每餐都要包含蛋白质食物；3.考虑蛋白质补充剂。");
        }
        
        // 基于营养平衡的建议
        Double balance = report.getNutritionBalance();
        if (balance != null && balance < 70) {
            recommendations.append("营养平衡建议：1.合理搭配三大营养素；2.多样化饮食；3.减少加工食品摄入。");
        }
        
        // 基于用户疾病的建议
        if (user.getDiseases() != null) {
            for (String disease : user.getDiseases()) {
                switch (disease) {
                    case "糖尿病":
                        recommendations.append("糖尿病建议：1.控制碳水化合物摄入；2.选择低GI食物；3.定时定量进餐。");
                        break;
                    case "高血压":
                        recommendations.append("高血压建议：1.低钠饮食；2.增加钾的摄入；3.控制体重。");
                        break;
                    case "高血脂":
                        recommendations.append("高血脂建议：1.减少饱和脂肪摄入；2.增加omega-3脂肪酸；3.多吃燕麦等降脂食物。");
                        break;
                }
            }
        }
        
        if (recommendations.length() == 0) {
            recommendations.append("您的营养摄入较为均衡，请继续保持良好的饮食习惯。");
        }
        
        report.setRecommendations(recommendations.toString());
    }
    
    /**
     * 获取用户的营养报告历史
     */
    public List<NutritionReport> getUserNutritionHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return nutritionReportRepository.findByUserOrderByReportDateDesc(user);
    }
    
    /**
     * 获取用户特定时间范围的营养报告
     */
    public List<NutritionReport> getUserNutritionReports(Long userId, LocalDateTime startDate, 
                                                        LocalDateTime endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return nutritionReportRepository.findByUserAndDateRange(user, startDate, endDate);
    }
    
    /**
     * 计算用户的平均健康得分
     */
    public Double getUserAverageHealthScore(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return nutritionReportRepository.calculateAverageHealthScoreByUser(user);
    }
} 