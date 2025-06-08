package com.restaurant.recommendation.controller;

import com.restaurant.recommendation.dto.RecommendationRequest;
import com.restaurant.recommendation.entity.Recommendation;
import com.restaurant.recommendation.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取个性化推荐
     */
    @PostMapping("/generate")
    public ResponseEntity<List<Recommendation>> generateRecommendations(
            @RequestBody RecommendationRequest request) {
        try {
            List<Recommendation> recommendations = recommendationService.generateRecommendations(request);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户推荐历史
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<Recommendation>> getRecommendationHistory(@PathVariable Long userId) {
        try {
            List<Recommendation> history = recommendationService.getUserRecommendationHistory(userId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 更新推荐反馈
     */
    @PutMapping("/{recommendationId}/feedback")
    public ResponseEntity<String> updateFeedback(
            @PathVariable Long recommendationId,
            @RequestParam(required = false, defaultValue = "false") boolean clicked,
            @RequestParam(required = false, defaultValue = "false") boolean ordered,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) String feedback) {
        try {
            recommendationService.updateRecommendationFeedback(
                recommendationId, clicked, ordered, rating, feedback);
            return ResponseEntity.ok("反馈更新成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失败：" + e.getMessage());
        }
    }
    
    /**
     * 简化版推荐接口 - 只需要用户ID
     */
    @GetMapping("/simple/{userId}")
    public ResponseEntity<List<Recommendation>> getSimpleRecommendations(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "10") Integer count) {
        try {
            RecommendationRequest request = new RecommendationRequest();
            request.setUserId(userId);
            request.setCount(count);
            
            List<Recommendation> recommendations = recommendationService.generateRecommendations(request);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 