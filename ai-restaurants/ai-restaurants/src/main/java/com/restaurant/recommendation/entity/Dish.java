package com.restaurant.recommendation.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "dishes")
public class Dish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;              // 菜名
    
    @Column(length = 1000)
    private String description;       // 菜品描述
    
    private BigDecimal price;         // 价格
    
    private String cuisine;           // 菜系 (川菜、粤菜、湘菜等)
    private String flavor;            // 口味 (辣、甜、酸、咸等)
    private String difficulty;        // 难度 (简单、中等、困难)
    private Integer cookingTime;      // 制作时间(分钟)
    
    // 营养成分 (每100g)
    private Double calories;          // 热量 (kcal)
    private Double protein;           // 蛋白质 (g)
    private Double fat;               // 脂肪 (g)
    private Double carbohydrate;      // 碳水化合物 (g)
    private Double fiber;             // 膳食纤维 (g)
    private Double sodium;            // 钠 (mg)
    private Double cholesterol;       // 胆固醇 (mg)
    
    // 食材列表
    @ElementCollection
    @CollectionTable(name = "dish_ingredients", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "ingredient")
    private List<String> ingredients;
    
    // 过敏原
    @ElementCollection
    @CollectionTable(name = "dish_allergens", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "allergen")
    private List<String> allergens;
    
    // 饮食标签
    @ElementCollection
    @CollectionTable(name = "dish_dietary_tags", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "tag")
    private List<String> dietaryTags;   // 素食、清真、低糖、低脂等
    
    // 适合的疾病人群
    @ElementCollection
    @CollectionTable(name = "dish_health_benefits", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "benefit")
    private List<String> healthBenefits; // 适合高血压、糖尿病等
    
    // 不适合的疾病人群
    @ElementCollection
    @CollectionTable(name = "dish_health_warnings", joinColumns = @JoinColumn(name = "dish_id"))
    @Column(name = "warning")
    private List<String> healthWarnings; // 不适合高血压、糖尿病等
    
    private Double rating;            // 评分 (1-5)
    private Integer reviewCount;      // 评价数量
    
    // 构造函数
    public Dish() {}
    
    public Dish(String name, String cuisine, String flavor) {
        this.name = name;
        this.cuisine = cuisine;
        this.flavor = flavor;
    }
    
    // 计算营养密度（蛋白质/热量比）
    public Double getProteinDensity() {
        if (protein != null && calories != null && calories > 0) {
            return protein / calories * 100;
        }
        return null;
    }
    
    // 判断是否为健康食品（简单规则）
    public Boolean isHealthy() {
        if (calories == null) return null;
        
        // 简单规则：低热量、高蛋白、低脂肪
        boolean lowCalorie = calories < 200;
        boolean highProtein = protein != null && protein > 15;
        boolean lowFat = fat != null && fat < 10;
        
        return lowCalorie && highProtein && lowFat;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }
    
    public String getFlavor() { return flavor; }
    public void setFlavor(String flavor) { this.flavor = flavor; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public Integer getCookingTime() { return cookingTime; }
    public void setCookingTime(Integer cookingTime) { this.cookingTime = cookingTime; }
    
    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }
    
    public Double getProtein() { return protein; }
    public void setProtein(Double protein) { this.protein = protein; }
    
    public Double getFat() { return fat; }
    public void setFat(Double fat) { this.fat = fat; }
    
    public Double getCarbohydrate() { return carbohydrate; }
    public void setCarbohydrate(Double carbohydrate) { this.carbohydrate = carbohydrate; }
    
    public Double getFiber() { return fiber; }
    public void setFiber(Double fiber) { this.fiber = fiber; }
    
    public Double getSodium() { return sodium; }
    public void setSodium(Double sodium) { this.sodium = sodium; }
    
    public Double getCholesterol() { return cholesterol; }
    public void setCholesterol(Double cholesterol) { this.cholesterol = cholesterol; }
    
    public List<String> getIngredients() { return ingredients; }
    public void setIngredients(List<String> ingredients) { this.ingredients = ingredients; }
    
    public List<String> getAllergens() { return allergens; }
    public void setAllergens(List<String> allergens) { this.allergens = allergens; }
    
    public List<String> getDietaryTags() { return dietaryTags; }
    public void setDietaryTags(List<String> dietaryTags) { this.dietaryTags = dietaryTags; }
    
    public List<String> getHealthBenefits() { return healthBenefits; }
    public void setHealthBenefits(List<String> healthBenefits) { this.healthBenefits = healthBenefits; }
    
    public List<String> getHealthWarnings() { return healthWarnings; }
    public void setHealthWarnings(List<String> healthWarnings) { this.healthWarnings = healthWarnings; }
    
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
} 