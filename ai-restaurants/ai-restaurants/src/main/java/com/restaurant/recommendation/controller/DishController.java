package com.restaurant.recommendation.controller;

import com.restaurant.recommendation.entity.Dish;
import com.restaurant.recommendation.repository.DishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dishes")
@CrossOrigin(origins = "*")
public class DishController {
    
    @Autowired
    private DishRepository dishRepository;
    
    /**
     * 创建菜品
     */
    @PostMapping
    public ResponseEntity<Dish> createDish(@RequestBody Dish dish) {
        try {
            Dish savedDish = dishRepository.save(dish);
            return ResponseEntity.ok(savedDish);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取所有菜品
     */
    @GetMapping
    public ResponseEntity<List<Dish>> getAllDishes() {
        List<Dish> dishes = dishRepository.findAll();
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 根据ID获取菜品
     */
    @GetMapping("/{id}")
    public ResponseEntity<Dish> getDishById(@PathVariable Long id) {
        Optional<Dish> dish = dishRepository.findById(id);
        return dish.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据菜系查找菜品
     */
    @GetMapping("/cuisine/{cuisine}")
    public ResponseEntity<List<Dish>> getDishesByCuisine(@PathVariable String cuisine) {
        List<Dish> dishes = dishRepository.findByCuisine(cuisine);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 根据口味查找菜品
     */
    @GetMapping("/flavor/{flavor}")
    public ResponseEntity<List<Dish>> getDishesByFlavor(@PathVariable String flavor) {
        List<Dish> dishes = dishRepository.findByFlavor(flavor);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 根据热量范围查找菜品
     */
    @GetMapping("/calories")
    public ResponseEntity<List<Dish>> getDishesByCaloriesRange(
            @RequestParam Double minCalories,
            @RequestParam Double maxCalories) {
        List<Dish> dishes = dishRepository.findDishesByCaloriesRange(minCalories, maxCalories);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 查找高蛋白菜品
     */
    @GetMapping("/high-protein")
    public ResponseEntity<List<Dish>> getHighProteinDishes(
            @RequestParam(defaultValue = "15.0") Double minProtein) {
        List<Dish> dishes = dishRepository.findHighProteinDishes(minProtein);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 查找低脂菜品
     */
    @GetMapping("/low-fat")
    public ResponseEntity<List<Dish>> getLowFatDishes(
            @RequestParam(defaultValue = "10.0") Double maxFat) {
        List<Dish> dishes = dishRepository.findLowFatDishes(maxFat);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 查找高评分菜品
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<Dish>> getTopRatedDishes(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        List<Dish> dishes = dishRepository.findTopRatedDishes(minRating);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 根据饮食标签查找菜品
     */
    @GetMapping("/dietary-tag/{tag}")
    public ResponseEntity<List<Dish>> getDishesByDietaryTag(@PathVariable String tag) {
        List<Dish> dishes = dishRepository.findDishesByDietaryTag(tag);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 查找不含特定过敏原的菜品
     */
    @GetMapping("/without-allergen/{allergen}")
    public ResponseEntity<List<Dish>> getDishesWithoutAllergen(@PathVariable String allergen) {
        List<Dish> dishes = dishRepository.findDishesWithoutAllergen(allergen);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 查找适合特定疾病的菜品
     */
    @GetMapping("/for-disease/{disease}")
    public ResponseEntity<List<Dish>> getDishesForDisease(@PathVariable String disease) {
        List<Dish> dishes = dishRepository.findDishesForDisease(disease);
        return ResponseEntity.ok(dishes);
    }
    
    /**
     * 更新菜品信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Dish> updateDish(@PathVariable Long id, @RequestBody Dish dishDetails) {
        try {
            Optional<Dish> optionalDish = dishRepository.findById(id);
            if (!optionalDish.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Dish dish = optionalDish.get();
            
            // 更新基本信息
            if (dishDetails.getName() != null) {
                dish.setName(dishDetails.getName());
            }
            if (dishDetails.getDescription() != null) {
                dish.setDescription(dishDetails.getDescription());
            }
            if (dishDetails.getPrice() != null) {
                dish.setPrice(dishDetails.getPrice());
            }
            if (dishDetails.getCuisine() != null) {
                dish.setCuisine(dishDetails.getCuisine());
            }
            if (dishDetails.getFlavor() != null) {
                dish.setFlavor(dishDetails.getFlavor());
            }
            
            // 更新营养信息
            if (dishDetails.getCalories() != null) {
                dish.setCalories(dishDetails.getCalories());
            }
            if (dishDetails.getProtein() != null) {
                dish.setProtein(dishDetails.getProtein());
            }
            if (dishDetails.getFat() != null) {
                dish.setFat(dishDetails.getFat());
            }
            if (dishDetails.getCarbohydrate() != null) {
                dish.setCarbohydrate(dishDetails.getCarbohydrate());
            }
            
            // 更新其他属性
            if (dishDetails.getIngredients() != null) {
                dish.setIngredients(dishDetails.getIngredients());
            }
            if (dishDetails.getAllergens() != null) {
                dish.setAllergens(dishDetails.getAllergens());
            }
            if (dishDetails.getDietaryTags() != null) {
                dish.setDietaryTags(dishDetails.getDietaryTags());
            }
            if (dishDetails.getHealthBenefits() != null) {
                dish.setHealthBenefits(dishDetails.getHealthBenefits());
            }
            if (dishDetails.getHealthWarnings() != null) {
                dish.setHealthWarnings(dishDetails.getHealthWarnings());
            }
            
            Dish updatedDish = dishRepository.save(dish);
            return ResponseEntity.ok(updatedDish);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        try {
            if (!dishRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            dishRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 