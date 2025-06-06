package com.bugvictims.demo11.Utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.Map;

public class JWTUtils {
    private static final String SECRET_KEY = "find-your-dazi-and-this-is-the-secret-key-and-the-now-is-just-for-add-length";
    private static final long EXPIRATION_TIME = 60 * 60 * 24 * 7 * 1000; // 时间 24hrs

    public static String generateToken(Map<String, Object> claims) { // 登录成功后，返回JWT令牌
        return JWT.create().withClaim("claims", claims).withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME)).sign(Algorithm.HMAC256(SECRET_KEY));
    }
    public static Map<String, Object> validateToken(String token) {// 检测JWT令牌是否有效，并返回JWT令牌所携带的信息
        return JWT.require(Algorithm.HMAC256(SECRET_KEY)).build().verify(token).getClaim("claims").asMap();
    }
    public static String getUsernameFromToken(String token) {
        Map<String, Object> claims = validateToken(token);
        return (String) claims.get("username");
    }

    public static int getUserIdFromToken(String token) {
        Map<String, Object> claims = validateToken(token);
        String userIdString = (String) claims.get("userId");  // 获取 String 类型的 userId
        try {
            return Integer.parseInt(userIdString);  // 将 String 转换为 int 类型
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format");  // 如果格式不对，抛出异常
        }
    }
}
