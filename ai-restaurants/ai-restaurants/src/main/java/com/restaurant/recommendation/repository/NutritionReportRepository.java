package com.restaurant.recommendation.repository;

import com.restaurant.recommendation.entity.NutritionReport;
import com.restaurant.recommendation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionReportRepository extends JpaRepository<NutritionReport, Long> {
    
    /**
     * 查找用户的营养报告，按日期降序排列
     */
    List<NutritionReport> findByUserOrderByReportDateDesc(User user);
    
    /**
     * 查找用户特定日期的营养报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.user = :user AND " +
           "DATE(n.reportDate) = DATE(:reportDate)")
    Optional<NutritionReport> findByUserAndReportDate(@Param("user") User user, 
                                                      @Param("reportDate") LocalDateTime reportDate);
    
    /**
     * 查找用户在特定时间范围内的营养报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.user = :user AND " +
           "n.reportDate BETWEEN :startDate AND :endDate ORDER BY n.reportDate DESC")
    List<NutritionReport> findByUserAndDateRange(@Param("user") User user,
                                                 @Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);
    
    /**
     * 查找用户最近的营养报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.user = :user ORDER BY n.reportDate DESC")
    List<NutritionReport> findRecentReportsByUser(@Param("user") User user);
    
    /**
     * 查找健康状态为特定状态的报告
     */
    List<NutritionReport> findByHealthStatus(String healthStatus);
    
    /**
     * 查找健康得分在特定范围内的报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.healthScore BETWEEN :minScore AND :maxScore")
    List<NutritionReport> findByHealthScoreRange(@Param("minScore") Integer minScore,
                                                 @Param("maxScore") Integer maxScore);
    
    /**
     * 统计用户的平均健康得分
     */
    @Query("SELECT AVG(n.healthScore) FROM NutritionReport n WHERE n.user = :user")
    Double calculateAverageHealthScoreByUser(@Param("user") User user);
    
    /**
     * 查找热量超标的报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.totalCalories > n.recommendedCalories * 1.1")
    List<NutritionReport> findCaloriesExceededReports();
    
    /**
     * 查找蛋白质不足的报告
     */
    @Query("SELECT n FROM NutritionReport n WHERE n.totalProtein < n.recommendedProtein * 0.9")
    List<NutritionReport> findProteinDeficientReports();
} 