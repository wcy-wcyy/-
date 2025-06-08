package com.restaurant.recommendation.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String email;
    
    // 健康数据
    private Double weight;      // 体重(kg)
    private Double height;      // 身高(cm)
    private Integer age;        // 年龄
    private String gender;      // 性别 M/F
    
    // 特殊健康状况
    @ElementCollection
    @CollectionTable(name = "user_diseases", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "disease")
    private List<String> diseases;  // 疾病列表
    
    // 饮食偏好
    @ElementCollection
    @CollectionTable(name = "user_preferred_cuisines", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "cuisine")
    private List<String> preferredCuisines;  // 喜欢的菜系
    
    @ElementCollection
    @CollectionTable(name = "user_preferred_flavors", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "flavor")
    private List<String> preferredFlavors;   // 喜欢的口味
    
    @ElementCollection
    @CollectionTable(name = "user_preferred_ingredients", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "ingredient")
    private List<String> preferredIngredients; // 喜欢的食材
    
    // 特殊饮食限制
    @ElementCollection
    @CollectionTable(name = "user_allergies", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "allergy")
    private List<String> allergies;          // 过敏食物
    
    @ElementCollection
    @CollectionTable(name = "user_dietary_restrictions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "restriction")
    private List<String> dietaryRestrictions; // 宗教或其他饮食限制
    
    // 构造函数
    public User() {}
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    // BMI计算
    public Double getBMI() {
        if (weight != null && height != null && height > 0) {
            return weight / Math.pow(height / 100, 2);
        }
        return null;
    }
    
    // 基础代谢率计算(简化版Harris-Benedict公式)
    public Double getBMR() {
        if (weight != null && height != null && age != null && gender != null) {
            if ("M".equalsIgnoreCase(gender)) {
                return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
            } else if ("F".equalsIgnoreCase(gender)) {
                return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
            }
        }
        return null;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public List<String> getDiseases() { return diseases; }
    public void setDiseases(List<String> diseases) { this.diseases = diseases; }
    
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
} 