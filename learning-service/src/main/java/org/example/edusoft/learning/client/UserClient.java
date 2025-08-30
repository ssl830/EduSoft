package org.example.edusoft.learning.client;

import org.example.edusoft.learning.exception.ServiceCallException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用户服务客户端
 * 负责调用user-service的相关接口
 */
@Component
public class UserClient extends BaseServiceClient {
    
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
     */
    public Map<String, Object> getUserById(Long userId) {
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
    
    /**
     * 兼容原有的方法（已废弃）
     * @deprecated 请使用 getUserById(Long userId)
     */
    @Deprecated
    public Map<String, Object> fetchUserById(String baseUrl, String token, Long userId) {
        return getUserById(userId);
    }
}
