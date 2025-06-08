package com.restaurant.recommendation.repository;

import com.restaurant.recommendation.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    
    /**
     * 根据菜系查找菜品
     */
    List<Dish> findByCuisine(String cuisine);
    
    /**
     * 根据口味查找菜品
     */
    List<Dish> findByFlavor(String flavor);
    
    /**
     * 根据菜系和口味查找菜品
     */
    List<Dish> findByCuisineAndFlavor(String cuisine, String flavor);
    
    /**
     * 查找包含特定食材的菜品
     */
    @Query("SELECT d FROM Dish d JOIN d.ingredients i WHERE i = :ingredient")
    List<Dish> findDishesContainingIngredient(@Param("ingredient") String ingredient);
    
    /**
     * 查找不包含特定过敏原的菜品
     */
    @Query("SELECT d FROM Dish d WHERE d.id NOT IN " +
           "(SELECT d2.id FROM Dish d2 JOIN d2.allergens a WHERE a = :allergen)")
    List<Dish> findDishesWithoutAllergen(@Param("allergen") String allergen);
    
    /**
     * 查找适合特定疾病的菜品
     */
    @Query("SELECT d FROM Dish d JOIN d.healthBenefits h WHERE h = :disease")
    List<Dish> findDishesForDisease(@Param("disease") String disease);
    
    /**
     * 查找不适合特定疾病的菜品
     */
    @Query("SELECT d FROM Dish d JOIN d.healthWarnings w WHERE w = :disease")
    List<Dish> findDishesNotForDisease(@Param("disease") String disease);
    
    /**
     * 根据热量范围查找菜品
     */
    @Query("SELECT d FROM Dish d WHERE d.calories BETWEEN :minCalories AND :maxCalories")
    List<Dish> findDishesByCaloriesRange(@Param("minCalories") Double minCalories, 
                                        @Param("maxCalories") Double maxCalories);
    
    /**
     * 查找高蛋白菜品
     */
    @Query("SELECT d FROM Dish d WHERE d.protein > :minProtein ORDER BY d.protein DESC")
    List<Dish> findHighProteinDishes(@Param("minProtein") Double minProtein);
    
    /**
     * 查找低脂菜品
     */
    @Query("SELECT d FROM Dish d WHERE d.fat < :maxFat ORDER BY d.fat ASC")
    List<Dish> findLowFatDishes(@Param("maxFat") Double maxFat);
    
    /**
     * 根据评分查找推荐菜品
     */
    @Query("SELECT d FROM Dish d WHERE d.rating >= :minRating ORDER BY d.rating DESC, d.reviewCount DESC")
    List<Dish> findTopRatedDishes(@Param("minRating") Double minRating);
    
    /**
     * 查找有特定饮食标签的菜品
     */
    @Query("SELECT d FROM Dish d JOIN d.dietaryTags t WHERE t = :tag")
    List<Dish> findDishesByDietaryTag(@Param("tag") String tag);
} 