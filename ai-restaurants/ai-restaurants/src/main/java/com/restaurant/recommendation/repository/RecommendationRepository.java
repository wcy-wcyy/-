package com.restaurant.recommendation.repository;

import com.restaurant.recommendation.entity.Recommendation;
import com.restaurant.recommendation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    
    /**
     * 查找用户的推荐记录
     */
    List<Recommendation> findByUserOrderByRecommendedAtDesc(User user);
    
    /**
     * 查找用户在特定时间范围内的推荐记录
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user = :user AND r.recommendedAt BETWEEN :startDate AND :endDate")
    List<Recommendation> findByUserAndDateRange(@Param("user") User user, 
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找用户点击过的推荐
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user = :user AND r.isClicked = true")
    List<Recommendation> findClickedRecommendationsByUser(@Param("user") User user);
    
    /**
     * 查找用户订购过的推荐
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user = :user AND r.isOrdered = true")
    List<Recommendation> findOrderedRecommendationsByUser(@Param("user") User user);
    
    /**
     * 查找高评分的推荐记录
     */
    @Query("SELECT r FROM Recommendation r WHERE r.score >= :minScore ORDER BY r.score DESC")
    List<Recommendation> findHighScoreRecommendations(@Param("minScore") Double minScore);
    
    /**
     * 统计用户的推荐点击率
     */
    @Query("SELECT COUNT(r) * 1.0 / (SELECT COUNT(r2) FROM Recommendation r2 WHERE r2.user = :user) " +
           "FROM Recommendation r WHERE r.user = :user AND r.isClicked = true")
    Double calculateClickRateByUser(@Param("user") User user);
    
    /**
     * 统计用户的推荐转化率
     */
    @Query("SELECT COUNT(r) * 1.0 / (SELECT COUNT(r2) FROM Recommendation r2 WHERE r2.user = :user) " +
           "FROM Recommendation r WHERE r.user = :user AND r.isOrdered = true")
    Double calculateConversionRateByUser(@Param("user") User user);
    
    /**
     * 查找最近的推荐记录
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user = :user ORDER BY r.recommendedAt DESC")
    List<Recommendation> findRecentRecommendationsByUser(@Param("user") User user);
} 