package org.example.edusoft.content.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * User Service Client
 * Used for communication with user-service
 */
@FeignClient(name = "user-service", url = "${user.service.url:http://localhost:8081}")
public interface UserClient {

    /**
     * Validate user identity
     * @param userId User ID
     * @return Validation result
     */
    @GetMapping("/api/user/validate/{userId}")
    Map<String, Object> validateUser(@PathVariable Long userId);

    /**
     * Get user information
     * @param userId User ID
     * @return User information
     */
    @GetMapping("/api/user/{userId}")
    Map<String, Object> getUserInfo(@PathVariable Long userId);

    /**
     * Check user permission
     * @param userId User ID
     * @param permission Permission name
     * @return Permission check result
     */
    @GetMapping("/api/user/{userId}/permission")
    Map<String, Object> checkUserPermission(@PathVariable Long userId, @RequestParam String permission);

    /**
     * Get batch user information
     * @param request Request containing user ID list
     * @return User information list
     */
    @PostMapping("/api/user/batch")
    Map<String, Object> getBatchUserInfo(@RequestBody Map<String, Object> request);

    /**
     * Check if user is teacher
     * @param userId User ID
     * @return Check result
     */
    @GetMapping("/api/user/{userId}/is-teacher")
    Map<String, Object> isTeacher(@PathVariable Long userId);

    /**
     * Check if user is student
     * @param userId User ID
     * @return Check result
     */
    @GetMapping("/api/user/{userId}/is-student")
    Map<String, Object> isStudent(@PathVariable Long userId);

    /**
     * Get user basic information
     * @param userId User ID
     * @return User basic information
     */
    @GetMapping("/api/user/{userId}/basic")
    Map<String, Object> getUserBasicInfo(@PathVariable Long userId);

    /**
     * Verify user login status
     * @param userId User ID
     * @param token User token
     * @return Verification result
     */
    @PostMapping("/api/user/verify-token")
    Map<String, Object> verifyUserToken(@RequestParam Long userId, @RequestParam String token);

    /**
     * Validate token and get current user info (for microservice-to-microservice)
     * Corresponds to user-service endpoint: GET /api/user/validate
     * Requires header: satoken
     * @param token Sa-Token value from request header "satoken"
     * @return SaResult-like map, expect data contains user fields
     */
    @GetMapping("/api/user/validate")
    Map<String, Object> validateToken(@RequestHeader("satoken") String token);
}
