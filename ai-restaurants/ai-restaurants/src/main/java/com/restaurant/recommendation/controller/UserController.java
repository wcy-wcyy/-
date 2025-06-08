package com.restaurant.recommendation.controller;

import com.restaurant.recommendation.entity.User;
import com.restaurant.recommendation.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 创建用户
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            // 检查用户名和邮箱是否已存在
            if (userRepository.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest().build();
            }
            if (userRepository.existsByEmail(user.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
            
            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取所有用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 根据用户名获取用户
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            Optional<User> optionalUser = userRepository.findById(id);
            if (!optionalUser.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = optionalUser.get();
            
            // 更新基本信息
            if (userDetails.getUsername() != null) {
                user.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                user.setEmail(userDetails.getEmail());
            }
            
            // 更新健康数据
            if (userDetails.getWeight() != null) {
                user.setWeight(userDetails.getWeight());
            }
            if (userDetails.getHeight() != null) {
                user.setHeight(userDetails.getHeight());
            }
            if (userDetails.getAge() != null) {
                user.setAge(userDetails.getAge());
            }
            if (userDetails.getGender() != null) {
                user.setGender(userDetails.getGender());
            }
            
            // 更新偏好和限制
            if (userDetails.getPreferredCuisines() != null) {
                user.setPreferredCuisines(userDetails.getPreferredCuisines());
            }
            if (userDetails.getPreferredFlavors() != null) {
                user.setPreferredFlavors(userDetails.getPreferredFlavors());
            }
            if (userDetails.getPreferredIngredients() != null) {
                user.setPreferredIngredients(userDetails.getPreferredIngredients());
            }
            if (userDetails.getAllergies() != null) {
                user.setAllergies(userDetails.getAllergies());
            }
            if (userDetails.getDietaryRestrictions() != null) {
                user.setDietaryRestrictions(userDetails.getDietaryRestrictions());
            }
            if (userDetails.getDiseases() != null) {
                user.setDiseases(userDetails.getDiseases());
            }
            
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * 获取用户的BMI
     */
    @GetMapping("/{id}/bmi")
    public ResponseEntity<Double> getUserBMI(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            Double bmi = user.get().getBMI();
            return ResponseEntity.ok(bmi);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * 获取用户的基础代谢率
     */
    @GetMapping("/{id}/bmr")
    public ResponseEntity<Double> getUserBMR(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            Double bmr = user.get().getBMR();
            return ResponseEntity.ok(bmr);
        }
        return ResponseEntity.notFound().build();
    }
}