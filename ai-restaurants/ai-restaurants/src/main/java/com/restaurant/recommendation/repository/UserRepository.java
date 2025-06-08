package com.restaurant.recommendation.repository;

import com.restaurant.recommendation.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);
    
    /**
     * 查找具有特定疾病的用户
     */
    @Query("SELECT u FROM User u JOIN u.diseases d WHERE d = :disease")
    List<User> findUsersByDisease(@Param("disease") String disease);
    
    /**
     * 查找喜欢特定菜系的用户
     */
    @Query("SELECT u FROM User u JOIN u.preferredCuisines c WHERE c = :cuisine")
    List<User> findUsersByPreferredCuisine(@Param("cuisine") String cuisine);
    
    /**
     * 查找有特定过敏的用户
     */
    @Query("SELECT u FROM User u JOIN u.allergies a WHERE a = :allergy")
    List<User> findUsersByAllergy(@Param("allergy") String allergy);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
} 