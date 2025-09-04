package org.example.edusoft.content.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

/**
 * Token拦截器
 * 用于从请求头中提取认证token并解析用户ID
 */
@Component
public class TokenInterceptor implements HandlerInterceptor {
    
    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 用户服务URL，用于验证token和获取用户ID
    private static final String USER_SERVICE_URL = "http://localhost:8081/api/user/token/validate";
    
    // 使用静态方法创建RestTemplate，避免循环依赖
    private RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * 从请求中提取认证token
     * 优先级：satoken > Authorization > free-fs-token
     */
    private String extractToken(HttpServletRequest request) {
        // 优先使用satoken头（符合Sa-Token框架）
        String satoken = request.getHeader("satoken");
        if (satoken != null && !satoken.trim().isEmpty()) {
            logger.debug("从satoken头获取token: {}", satoken);
            return satoken;
        }

        // 其次使用Authorization头
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.trim().isEmpty()) {
            logger.debug("从Authorization头获取token: {}", authHeader);
            // 兼容 Bearer 前缀
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7).trim();
            }
            return authHeader;
        }

        // 最后尝试free-fs-token头（前端使用的格式）
        String freeFsToken = request.getHeader("free-fs-token");
        if (freeFsToken != null && !freeFsToken.trim().isEmpty()) {
            logger.debug("从free-fs-token头获取token: {}", freeFsToken);
            return freeFsToken;
        }

        logger.debug("未找到有效的认证token");
        return null;
    }
    
    /**
     * 从token中解析用户ID
     * 支持多种格式，现在返回String类型的userId
     * 1. userId_timestamp_random (下划线分隔)
     * 2. JWT格式 (尝试解析payload中的用户信息)
     * 3. 纯数字格式
     * 4. 字符串格式 (如 T001, S001)
     * 5. UUID格式 (如 8ab64563-8f9f-4eae-a14b-c1096552bf67)
     */
    private String extractUserIdFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 方法1: 优先尝试解析为UUID格式 (如 8ab64563-8f9f-4eae-a14b-c1096552bf67)
            String userId = parseUUIDFormat(token);
            if (userId != null) {
                logger.debug("从UUID格式token解析出用户ID: {}", userId);
                return userId;
            }
            
            // 方法2: 尝试解析为下划线分隔格式 (userId_timestamp_random)
            userId = parseUnderscoreFormat(token);
            if (userId != null) {
                logger.debug("从下划线格式token解析出用户ID: {}", userId);
                return userId;
            }
            
            // 方法3: 尝试解析为JWT格式
            userId = parseJWTFormat(token);
            if (userId != null) {
                logger.debug("从JWT格式token解析出用户ID: {}", userId);
                return userId;
            }
            
            // 方法4: 尝试解析为字符串格式 (如 T001, S001)
            userId = parseStringFormat(token);
            if (userId != null) {
                logger.debug("从字符串格式token解析出用户ID: {}", userId);
                return userId;
            }
            
            // 方法5: 最后尝试解析为纯数字格式
            userId = parseNumericFormat(token);
            if (userId != null) {
                logger.debug("从纯数字格式token解析出用户ID: {}", userId);
                return userId;
            }
            
        } catch (Exception e) {
            logger.warn("解析token中的用户ID失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 解析下划线分隔格式: userId_timestamp_random
     */
    private String parseUnderscoreFormat(String token) {
        try {
            // 首先检查是否是UUID格式，如果是则跳过
            if (token.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                logger.debug("token是UUID格式，跳过下划线格式解析: {}", token);
                return null;
            }
            
            String[] parts = token.split("_");
            if (parts.length >= 1) {
                String userIdStr = parts[0];
                logger.debug("从下划线格式token解析出用户ID: {}", userIdStr);
                // 直接返回字符串，不转换为Long
                return userIdStr;
            }
        } catch (Exception e) {
            logger.debug("下划线格式解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析JWT格式token
     */
    private String parseJWTFormat(String token) {
        try {
            // JWT格式: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                // 解码payload部分
                String payload = new String(Base64.getDecoder().decode(parts[1]));
                Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);
                
                // 尝试多种可能的用户ID字段
                Object userIdObj = payloadMap.get("userId");
                if (userIdObj == null) userIdObj = payloadMap.get("user_id");
                if (userIdObj == null) userIdObj = payloadMap.get("id");
                if (userIdObj == null) userIdObj = payloadMap.get("sub");
                
                if (userIdObj != null) {
                    if (userIdObj instanceof Number) {
                        return userIdObj.toString();
                    } else if (userIdObj instanceof String) {
                        // 如果是字符串，直接返回
                        return (String) userIdObj;
                    }
                }
            }
        } catch (Exception e) {
            logger.debug("JWT格式解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析纯数字格式
     */
    private String parseNumericFormat(String token) {
        try {
            // 移除可能的空格和特殊字符
            String cleanToken = token.trim().replaceAll("[^0-9]", "");
            if (!cleanToken.isEmpty()) {
                logger.debug("成功解析纯数字格式token为用户ID: {}", cleanToken);
                return cleanToken;
            }
        } catch (Exception e) {
            logger.debug("纯数字格式解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析字符串格式 (如 T001, S001)
     * 这里需要将字符串格式的userId转换为对应的数字ID
     * 由于无法直接查询数据库，我们使用一个映射表或默认值
     */
    private String parseStringFormat(String token) {
        try {
            String cleanToken = token.trim();
            if (cleanToken.isEmpty()) {
                return null;
            }
            
            // 检查是否是字符串格式 (如 T001, S001, Admin1)
            if (cleanToken.matches("^[A-Za-z]+\\d+$")) {
                logger.debug("检测到字符串格式的userId: {}", cleanToken);
                return cleanToken; // 直接返回字符串格式
            }
            
        } catch (Exception e) {
            logger.debug("字符串格式解析失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 解析UUID格式token
     * 通过调用用户服务验证token并获取字符串格式的userId
     */
    private String parseUUIDFormat(String token) {
        try {
            String cleanToken = token.trim();
            if (cleanToken.isEmpty()) {
                return null;
            }
            
            // 检查是否是UUID格式 (如 8ab64563-8f9f-4eae-a14b-c1096552bf67)
            if (cleanToken.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                logger.debug("检测到UUID格式的token: {}", cleanToken);
                
                // 调用用户服务验证token并获取字符串格式的userId
                logger.debug("开始调用用户服务验证token: {}", cleanToken);
                String userId = validateTokenWithUserService(cleanToken);
                if (userId != null) {
                    logger.debug("通过用户服务验证token成功，获取到用户ID: {}", userId);
                    return userId;
                } else {
                    logger.warn("通过用户服务验证token失败，无法获取用户ID");
                    logger.debug("用户服务返回null，可能是网络问题或服务不可用");
                }
            }
            
        } catch (Exception e) {
            logger.debug("UUID格式解析失败: {}", e.getMessage());
            logger.error("UUID格式解析异常: ", e);
        }
        return null;
    }

    /**
     * 从认证信息中提取用户ID
     * 尝试从各种可能的认证信息中获取用户ID
     */
    private String extractUserIdFromAuthInfo(HttpServletRequest request) {
        try {
            // 尝试从Authorization头中获取Bearer token
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String bearerToken = authHeader.substring(7).trim();
                logger.debug("尝试从Bearer token中提取用户ID: {}", bearerToken);
                
                // 这里可以添加更多的认证逻辑
                // 例如：解析JWT、查询数据库等
                
                // 临时方案：如果token是UUID格式，尝试从数据库查询对应的用户
                if (bearerToken.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
                    logger.debug("检测到UUID格式的Bearer token，尝试查询数据库");
                    
                    // 在实际生产环境中，这里应该查询数据库获取UUID对应的用户ID
                    // 由于我们无法直接访问数据库，这里返回null
                    logger.warn("UUID格式的token需要查询数据库获取用户ID，当前无法实现");
                    return null;
                }
            }
            
            // 尝试从其他认证头中获取
            String satoken = request.getHeader("satoken");
            if (satoken != null && !satoken.trim().isEmpty()) {
                logger.debug("尝试从satoken中提取用户ID: {}", satoken);
                // 这里可以添加sa-token的解析逻辑
            }
            
        } catch (Exception e) {
            logger.debug("从认证信息中提取用户ID失败: {}", e.getMessage());
        }
        
        return null;
    }

    /**
     * 调用用户服务验证token并获取字符串格式的userId
     */
    private String validateTokenWithUserService(String token) {
        try {
            logger.debug("开始调用用户服务验证token: {}", token);
            logger.debug("用户服务URL: {}", USER_SERVICE_URL);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("satoken", token);
            
            // 创建请求实体
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            // 调用用户服务的验证接口
            ResponseEntity<Map> response = getRestTemplate().exchange(
                USER_SERVICE_URL, 
                HttpMethod.GET, 
                requestEntity, 
                Map.class
            );
            
            logger.debug("用户服务响应状态: {}", response.getStatusCode());
            logger.debug("用户服务响应体: {}", response.getBody());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                logger.debug("响应体类型: {}", responseBody.getClass().getName());
                logger.debug("响应体所有键: {}", responseBody.keySet());
                
                // 检查响应状态
                if (responseBody.containsKey("code") && responseBody.get("code").equals(200)) {
                    // 获取用户数据
                    Object dataObj = responseBody.get("data");
                    logger.debug("data字段: {}", dataObj);
                    logger.debug("data字段类型: {}", dataObj != null ? dataObj.getClass().getName() : "null");
                    
                    Map<String, Object> userData = null;
                    if (dataObj instanceof Map) {
                        userData = (Map<String, Object>) dataObj;
                        logger.debug("用户数据Map: {}", userData);
                        logger.debug("用户数据Map的所有键: {}", userData.keySet());
                    } else {
                        logger.warn("data字段不是Map类型，无法解析用户数据");
                        return null;
                    }
                    
                    if (userData != null && userData.containsKey("userId")) {
                        // 优先获取userId字段（字符串格式，如20200207, T001, S001）
                        Object userIdObj = userData.get("userId");
                        logger.debug("找到userId字段: {}", userIdObj);
                        logger.debug("userId字段类型: {}", userIdObj != null ? userIdObj.getClass().getName() : "null");
                        if (userIdObj instanceof String) {
                            logger.debug("成功获取userId: {}", userIdObj);
                            return (String) userIdObj;
                        }
                    } else if (userData != null && userData.containsKey("userid")) {
                        // 兼容userid字段（小写）
                        Object userIdObj = userData.get("userid");
                        logger.debug("找到userid字段: {}", userIdObj);
                        logger.debug("userid字段类型: {}", userIdObj != null ? userIdObj.getClass().getName() : "null");
                        if (userIdObj instanceof String) {
                            logger.debug("成功获取userid: {}", userIdObj);
                            return (String) userIdObj;
                        }
                    } else if (userData != null && userData.containsKey("id")) {
                        // 如果没有userId字段，使用id字段
                        Object userIdObj = userData.get("id");
                        logger.debug("找到id字段: {}", userIdObj);
                        logger.debug("id字段类型: {}", userIdObj != null ? userIdObj.getClass().getName() : "null");
                        if (userIdObj instanceof Number) {
                            String userIdStr = userIdObj.toString();
                            logger.debug("将id字段转换为字符串: {}", userIdStr);
                            return userIdStr;
                        } else if (userIdObj instanceof String) {
                            logger.debug("成功获取id字段(字符串): {}", userIdObj);
                            return (String) userIdObj;
                        }
                    }
                    
                    logger.warn("用户数据中没有找到有效的用户ID字段");
                    logger.debug("用户数据内容: {}", userData);
                } else {
                    logger.warn("用户服务返回错误: {}", responseBody.get("msg"));
                }
            } else {
                logger.warn("用户服务响应不成功: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("调用用户服务验证token失败: {}", e.getMessage());
            logger.error("异常详情: ", e);
        }
        
        return null;
    }

    private Long tryParseLong(String value) {
        try {
            if (value == null || value.trim().isEmpty()) return null;
            return Long.valueOf(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestPath = request.getRequestURI();
        logger.debug("Token拦截器处理请求: {} {}", request.getMethod(), requestPath);

        // 1) 优先从请求头/参数中直接读取 userId
        String userId = request.getHeader("userId");
        if (userId == null) userId = request.getHeader("X-User-Id");
        if (userId == null) userId = request.getParameter("userId");

        // 2) 如果没有，则从token中解析
        if (userId == null) {
            String token = extractToken(request);
            if (token != null && !token.trim().isEmpty()) {
                logger.debug("开始解析token: {}", token);
                userId = extractUserIdFromToken(token);
                logger.debug("token解析结果: {}", userId);
            }
        }

        // 3) 如果仍然没有，尝试从认证信息中获取
        if (userId == null) {
            userId = extractUserIdFromAuthInfo(request);
        }

        if (userId != null) {
            // 将用户ID存储到请求属性中，供后续使用
            request.setAttribute("userId", userId);
            logger.debug("已解析到用户ID并写入请求属性: {}", userId);
        } else {
            logger.warn("未能获取到用户ID（header/query/token/auth皆不可用）");
        }

        // 这个拦截器不阻止请求，只是解析token
        return true;
    }
}
