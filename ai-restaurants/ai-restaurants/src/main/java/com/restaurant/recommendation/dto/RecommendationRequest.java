package com.restaurant.recommendation.dto;

import java.util.List;

public class RecommendationRequest {
    private Long userId;
    private List<String> preferredCuisines;
    private List<String> preferredFlavors;
    private List<String> preferredIngredients;
    private List<String> allergies;
    private List<String> dietaryRestrictions;
    private Integer maxCalories;
    private Integer minProtein;
    private Integer maxFat;
    private String mealType;  // 早餐、午餐、晚餐、加餐
    private Integer count;    // 推荐数量
    
    // 构造函数
    public RecommendationRequest() {
        this.count = 10; // 默认推荐10个菜品
    }
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public List<String> getPreferredCuisines() { return preferredCuisines; }
    public void setPreferredCuisines(List<String> preferredCuisines) { this.preferredCuisines = preferredCuisines; }
    
    public List<String> getPreferredFlavors() { return preferredFlavors; }
    public void setPreferredFlavors(List<String> preferredFlavors) { this.preferredFlavors = preferredFlavors; }
    
    public List<String> getPreferredIngredients() { return preferredIngredients; }
    public void setPreferredIngredients(List<String> preferredIngredients) { this.preferredIngredients = preferredIngredients; }
    
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
    
    public List<String> getDietaryRestrictions() { return dietaryRestrictions; }
    public void setDietaryRestrictions(List<String> dietaryRestrictions) { this.dietaryRestrictions = dietaryRestrictions; }
    
    public Integer getMaxCalories() { return maxCalories; }
    public void setMaxCalories(Integer maxCalories) { this.maxCalories = maxCalories; }
    
    public Integer getMinProtein() { return minProtein; }
    public void setMinProtein(Integer minProtein) { this.minProtein = minProtein; }
    
    public Integer getMaxFat() { return maxFat; }
    public void setMaxFat(Integer maxFat) { this.maxFat = maxFat; }
    
    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }
    
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
} 