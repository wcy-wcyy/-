package com.restaurant.recommendation.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;
    
    private Double score;                    // 推荐得分 (0-100)
    private String reason;                   // 推荐理由
    private LocalDateTime recommendedAt;     // 推荐时间
    
    // 匹配度详情
    private Double cuisineMatch;             // 菜系匹配度
    private Double flavorMatch;              // 口味匹配度
    private Double ingredientMatch;          // 食材匹配度
    private Double healthMatch;              // 健康匹配度
    private Double nutritionMatch;           // 营养匹配度
    
    private Boolean isClicked;               // 是否被点击
    private Boolean isOrdered;               // 是否被订购
    private Integer userRating;              // 用户评分 (1-5)
    private String userFeedback;             // 用户反馈
    
    // 构造函数
    public Recommendation() {
        this.recommendedAt = LocalDateTime.now();
        this.isClicked = false;
        this.isOrdered = false;
    }
    
    public Recommendation(User user, Dish dish, Double score, String reason) {
        this();
        this.user = user;
        this.dish = dish;
        this.score = score;
        this.reason = reason;
    }
    
    // 计算综合匹配度
    public Double getOverallMatch() {
        double total = 0;
        int count = 0;
        
        if (cuisineMatch != null) { total += cuisineMatch; count++; }
        if (flavorMatch != null) { total += flavorMatch; count++; }
        if (ingredientMatch != null) { total += ingredientMatch; count++; }
        if (healthMatch != null) { total += healthMatch; count++; }
        if (nutritionMatch != null) { total += nutritionMatch; count++; }
        
        return count > 0 ? total / count : 0.0;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Dish getDish() { return dish; }
    public void setDish(Dish dish) { this.dish = dish; }
    
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LocalDateTime getRecommendedAt() { return recommendedAt; }
    public void setRecommendedAt(LocalDateTime recommendedAt) { this.recommendedAt = recommendedAt; }
    
    public Double getCuisineMatch() { return cuisineMatch; }
    public void setCuisineMatch(Double cuisineMatch) { this.cuisineMatch = cuisineMatch; }
    
    public Double getFlavorMatch() { return flavorMatch; }
    public void setFlavorMatch(Double flavorMatch) { this.flavorMatch = flavorMatch; }
    
    public Double getIngredientMatch() { return ingredientMatch; }
    public void setIngredientMatch(Double ingredientMatch) { this.ingredientMatch = ingredientMatch; }
    
    public Double getHealthMatch() { return healthMatch; }
    public void setHealthMatch(Double healthMatch) { this.healthMatch = healthMatch; }
    
    public Double getNutritionMatch() { return nutritionMatch; }
    public void setNutritionMatch(Double nutritionMatch) { this.nutritionMatch = nutritionMatch; }
    
    public Boolean getIsClicked() { return isClicked; }
    public void setIsClicked(Boolean isClicked) { this.isClicked = isClicked; }
    
    public Boolean getIsOrdered() { return isOrdered; }
    public void setIsOrdered(Boolean isOrdered) { this.isOrdered = isOrdered; }
    
    public Integer getUserRating() { return userRating; }
    public void setUserRating(Integer userRating) { this.userRating = userRating; }
    
    public String getUserFeedback() { return userFeedback; }
    public void setUserFeedback(String userFeedback) { this.userFeedback = userFeedback; }
} 