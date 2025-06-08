package com.restaurant.recommendation.controller;

import com.restaurant.recommendation.entity.Dish;
import com.restaurant.recommendation.entity.NutritionReport;
import com.restaurant.recommendation.repository.DishRepository;
import com.restaurant.recommendation.service.NutritionAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
public class NutritionController {
    
    @Autowired
    private NutritionAnalysisService nutritionAnalysisService;
    
    @Autowired
    private DishRepository dishRepository;
    
    /**
     * 生成营养分析报告
     */
    @PostMapping("/report/{userId}")
    public ResponseEntity<NutritionReport> generateNutritionReport(
            @PathVariable Long userId,
            @RequestBody List<Long> dishIds,
            @RequestParam(required = false) String reportDate) {
        try {
            List<Dish> dishes = dishIds.stream()
                    .map(id -> dishRepository.findById(id))
                    .filter(opt -> opt.isPresent())
                    .map(opt -> opt.get())
                    .collect(Collectors.toList());
            
            LocalDateTime date = reportDate != null ? 
                LocalDateTime.parse(reportDate) : LocalDateTime.now();
            
            NutritionReport report = nutritionAnalysisService
                    .generateNutritionReport(userId, dishes, date);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户营养报告历史
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<NutritionReport>> getNutritionHistory(@PathVariable Long userId) {
        try {
            List<NutritionReport> history = nutritionAnalysisService.getUserNutritionHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户平均健康得分
     */
    @GetMapping("/health-score/{userId}")
    public ResponseEntity<Double> getAverageHealthScore(@PathVariable Long userId) {
        try {
            Double score = nutritionAnalysisService.getUserAverageHealthScore(userId);
            return ResponseEntity.ok(score != null ? score : 0.0);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 快速生成今日营养报告（使用示例菜品）
     */
    @PostMapping("/quick-report/{userId}")
    public ResponseEntity<NutritionReport> generateQuickReport(@PathVariable Long userId) {
        try {
            // 获取一些示例菜品来生成报告
            List<Dish> sampleDishes = dishRepository.findAll().stream()
                    .limit(3)
                    .collect(Collectors.toList());
            
            NutritionReport report = nutritionAnalysisService
                    .generateNutritionReport(userId, sampleDishes, LocalDateTime.now());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}