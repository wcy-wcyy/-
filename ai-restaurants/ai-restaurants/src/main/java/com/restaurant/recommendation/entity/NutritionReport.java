package com.restaurant.recommendation.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "nutrition_reports")
public class NutritionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    private LocalDateTime generatedAt;       // 报告生成时间
    private LocalDateTime reportDate;        // 报告日期
    
    // 当日营养摄入汇总
    private Double totalCalories;            // 总热量 (kcal)
    private Double totalProtein;             // 总蛋白质 (g)
    private Double totalFat;                 // 总脂肪 (g)
    private Double totalCarbohydrate;        // 总碳水化合物 (g)
    private Double totalFiber;               // 总膳食纤维 (g)
    private Double totalSodium;              // 总钠 (mg)
    private Double totalCholesterol;         // 总胆固醇 (mg)
    
    // 推荐摄入量对比
    private Double recommendedCalories;      // 推荐热量
    private Double recommendedProtein;       // 推荐蛋白质
    private Double recommendedFat;           // 推荐脂肪
    private Double recommendedCarbohydrate;  // 推荐碳水化合物
    
    // 营养比例分析
    private Double proteinPercentage;        // 蛋白质热量占比
    private Double fatPercentage;            // 脂肪热量占比
    private Double carbohydratePercentage;   // 碳水化合物热量占比
    
    // 健康评估
    private String healthStatus;             // 健康状态 (健康、需改善、不健康)
    private Integer healthScore;             // 健康得分 (0-100)
    
    @Column(length = 2000)
    private String recommendations;          // 营养建议
    
    @Column(length = 1000)
    private String warnings;                 // 健康警告
    
    // 包含的菜品列表
    @ElementCollection
    @CollectionTable(name = "report_dishes", joinColumns = @JoinColumn(name = "report_id"))
    @Column(name = "dish_name")
    private List<String> includedDishes;
    
    // 构造函数
    public NutritionReport() {
        this.generatedAt = LocalDateTime.now();
    }
    
    public NutritionReport(User user, LocalDateTime reportDate) {
        this();
        this.user = user;
        this.reportDate = reportDate;
    }
    
    // 计算营养摄入是否超标
    public Boolean isCaloriesExceeded() {
        if (totalCalories != null && recommendedCalories != null) {
            return totalCalories > recommendedCalories * 1.1; // 超过10%认为超标
        }
        return false;
    }
    
    // 计算蛋白质摄入是否充足
    public Boolean isProteinAdequate() {
        if (totalProtein != null && recommendedProtein != null) {
            return totalProtein >= recommendedProtein * 0.9; // 达到90%认为充足
        }
        return false;
    }
    
    // 计算营养平衡度
    public Double getNutritionBalance() {
        if (proteinPercentage == null || fatPercentage == null || carbohydratePercentage == null) {
            return null;
        }
        
        // 理想比例：蛋白质10-35%，脂肪20-35%，碳水化合物45-65%
        double proteinDeviation = Math.abs(proteinPercentage - 22.5); // 理想值22.5%
        double fatDeviation = Math.abs(fatPercentage - 27.5);          // 理想值27.5%
        double carbDeviation = Math.abs(carbohydratePercentage - 55);  // 理想值55%
        
        double totalDeviation = proteinDeviation + fatDeviation + carbDeviation;
        return Math.max(0, 100 - totalDeviation); // 偏差越小，平衡度越高
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    
    public Double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(Double totalCalories) { this.totalCalories = totalCalories; }
    
    public Double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(Double totalProtein) { this.totalProtein = totalProtein; }
    
    public Double getTotalFat() { return totalFat; }
    public void setTotalFat(Double totalFat) { this.totalFat = totalFat; }
    
    public Double getTotalCarbohydrate() { return totalCarbohydrate; }
    public void setTotalCarbohydrate(Double totalCarbohydrate) { this.totalCarbohydrate = totalCarbohydrate; }
    
    public Double getTotalFiber() { return totalFiber; }
    public void setTotalFiber(Double totalFiber) { this.totalFiber = totalFiber; }
    
    public Double getTotalSodium() { return totalSodium; }
    public void setTotalSodium(Double totalSodium) { this.totalSodium = totalSodium; }
    
    public Double getTotalCholesterol() { return totalCholesterol; }
    public void setTotalCholesterol(Double totalCholesterol) { this.totalCholesterol = totalCholesterol; }
    
    public Double getRecommendedCalories() { return recommendedCalories; }
    public void setRecommendedCalories(Double recommendedCalories) { this.recommendedCalories = recommendedCalories; }
    
    public Double getRecommendedProtein() { return recommendedProtein; }
    public void setRecommendedProtein(Double recommendedProtein) { this.recommendedProtein = recommendedProtein; }
    
    public Double getRecommendedFat() { return recommendedFat; }
    public void setRecommendedFat(Double recommendedFat) { this.recommendedFat = recommendedFat; }
    
    public Double getRecommendedCarbohydrate() { return recommendedCarbohydrate; }
    public void setRecommendedCarbohydrate(Double recommendedCarbohydrate) { this.recommendedCarbohydrate = recommendedCarbohydrate; }
    
    public Double getProteinPercentage() { return proteinPercentage; }
    public void setProteinPercentage(Double proteinPercentage) { this.proteinPercentage = proteinPercentage; }
    
    public Double getFatPercentage() { return fatPercentage; }
    public void setFatPercentage(Double fatPercentage) { this.fatPercentage = fatPercentage; }
    
    public Double getCarbohydratePercentage() { return carbohydratePercentage; }
    public void setCarbohydratePercentage(Double carbohydratePercentage) { this.carbohydratePercentage = carbohydratePercentage; }
    
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    
    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public String getWarnings() { return warnings; }
    public void setWarnings(String warnings) { this.warnings = warnings; }
    
    public List<String> getIncludedDishes() { return includedDishes; }
    public void setIncludedDishes(List<String> includedDishes) { this.includedDishes = includedDishes; }
} 