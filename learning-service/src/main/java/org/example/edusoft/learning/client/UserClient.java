package org.example.edusoft.learning.client;

import org.example.edusoft.learning.exception.ServiceCallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * 用户服务客户端
 * 负责调用user-service的相关接口
 */
@Component
public class UserClient extends BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(UserClient.class);

    @Value("${service.user.url}")
    private String userServiceUrl;
    
    @Override
    protected String getServiceName() {
        return "user-service";
    }
    
    @Override
    protected String getBaseUrl() {
        return userServiceUrl;
    }

    /**
     * 根据用户ID获取用户信息
     * @param token 认证token（支持Bearer前缀或原始token）
     * @param userId 用户ID
     * @return 用户信息Map，如果失败返回null
     */
    public Map<String, Object> fetchUserById(String baseUrl, String token, String userId) {
        if (token == null || token.trim().isEmpty()) {
            logger.warn("Token is null，为空，无法获取用户信息");
            return null;
        }

        // 提取纯token（去掉Bearer前缀）
        String pureToken = token.replace("Bearer ", "");

        // 尝试多个可能的API端点，优先使用通过数据库ID查询的接口
        String[] candidates = {
                baseUrl + "/api/user/id/" + userId,  // 通过数据库ID查询（新增接口）
                baseUrl + "/api/user/" + userId,     // 通过用户编码查询（原有接口）
                baseUrl + "/api/user/info/" + userId,
                baseUrl + "/user/" + userId
        };

        HttpStatusCodeException lastEx = null;

        for (String url : candidates) {
            try {
                HttpHeaders headers = new HttpHeaders();

                // 优先使用satoken头（符合Sa-Token框架）
                headers.set("satoken", pureToken);

                // 同时设置Authorization头作为备选
                headers.set("Authorization", "Bearer " + pureToken);

                logger.debug("尝试请求用户服务，URL: {}, UserId: {}, Token: {}", url, userId, pureToken);

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                if (response.getBody() != null) {
                    logger.debug("成功获取用户信息: {}", response.getBody());

                    // 处理SaResult格式的响应
                    Map<String, Object> result = response.getBody();
                    Integer code = (Integer) result.get("code");

                    if (code != null && code == 200) {
                        // 成功响应，返回data部分
                        Object data = result.get("data");
                        if (data instanceof Map) {
                            return (Map<String, Object>) data;
                        } else {
                            logger.warn("响应data不是Map类型: {}", data);
                            continue; // 尝试下一个端点
                        }
                    } else {
                        logger.debug("用户服务返回错误: {}", result);
                        continue; // 尝试下一个端点
                    }
                }
            } catch (HttpStatusCodeException ex) {
                logger.debug("尝试URL {} 失败: {} {}", url, ex.getStatusCode(), ex.getResponseBodyAsString());
                lastEx = ex;
            } catch (Exception ex) {
                logger.debug("请求URL {} 时发生异常: {}", url, ex.getMessage());
            }
        }

        // 所有端点都失败了
        if (lastEx != null) {
            logger.warn("所有用户服务端点都失败，最后错误: {} {}", lastEx.getStatusCode(), lastEx.getResponseBodyAsString());
        }
        return null;
    }

    /**
     * 根据用户ID获取用户信息
     */
    public Map<String, Object>  getUserById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        // 尝试多个可能的API路径
        String[] candidatePaths = {
            "/api/user/" + userId,
            "/api/user/info/" + userId,
            "/user/" + userId
        };
        
        ServiceCallException lastException = null;
        for (String path : candidatePaths) {
            try {
                return getForMap(path);
            } catch (ServiceCallException ex) {
                lastException = ex;
                // 如果是404，尝试下一个路径；其他错误直接抛出
                if (ex.getStatusCode() != 404) {
                    throw ex;
                }
            }
        }
        
        // 所有路径都失败了
        if (lastException != null) {
            throw lastException;
        }
        throw new ServiceCallException(getServiceName(), "用户服务不可用");
    }
    
    /**
     * 获取用户详细信息（包含角色信息）
     */
    public Map<String, Object> getUserDetail(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return getForMap("/api/user/detail/" + userId);
    }
    
    /**
     * 批量获取用户信息
     */
    public Map<String, Object> getUsersByIds(String userIds) {
        if (userIds == null || userIds.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID列表不能为空");
        }
        return getForMap("/api/user/batch?ids=" + userIds);
    }

    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    /**
     * 验证用户是否存在
     */
    public boolean userExists2(Long userId) {
        try {
            String token = resolveOutboundToken();
            fetchUserById(userServiceBaseUrl, token, String.valueOf(userId));
            return true;
        } catch (ServiceCallException ex) {
            if (ex.getStatusCode() == 404) {
                return false;
            }
            throw ex; // 其他错误重新抛出
        }
    }
    
    /**
     * 验证用户是否存在
     */
    public boolean userExists(Long userId) {
        try {
            getUserById(userId);
            return true;
        } catch (ServiceCallException ex) {
            if (ex.getStatusCode() == 404) {
                return false;
            }
            throw ex; // 其他错误重新抛出
        }
    }
    
//    /**
//     * 兼容原有的方法（已废弃）
//     * @deprecated 请使用 getUserById(Long userId)
//     */
//    @Deprecated
//    public Map<String, Object> fetchUserById(String baseUrl, String token, Long userId) {
//        return getUserById(userId);
//    }
}
