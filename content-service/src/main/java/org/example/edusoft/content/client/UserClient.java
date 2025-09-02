package org.example.edusoft.content.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserClient {
	private static final Logger logger = LoggerFactory.getLogger(UserClient.class);
	private final RestTemplate restTemplate = new RestTemplate();

	/**
	 * 根据用户ID获取用户信息
	 * @param baseUrl 用户服务基础URL
	 * @param token 认证token（支持Bearer前缀或原始token）
	 * @param userId 用户ID
	 * @return 用户信息Map，如果失败返回null
	 */
	public Map<String, Object> fetchUserById(String baseUrl, String token, String userId) {
		if (token == null || token.trim().isEmpty()) {
			logger.warn("Token为空，无法获取用户信息");
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
				
				// 只使用satoken头
				headers.set("satoken", pureToken);
				
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
	 * 通过token获取当前用户信息 
	 * @param baseUrl 用户服务基础URL
	 * @param token 认证token（支持Bearer前缀或原始token）
	 * @return 用户信息Map，如果失败返回null
	 */
	public Map<String, Object> fetchCurrentUser(String baseUrl, String token) {
		if (token == null || token.trim().isEmpty()) {
			logger.warn("Token为空，无法获取用户信息");
			return null;
		}

		// 提取纯token（去掉Bearer前缀）
		String pureToken = token.replace("Bearer ", "");
		
		try {
			HttpHeaders headers = new HttpHeaders();
			
			// 只使用satoken头
			headers.set("satoken", pureToken);
			
			logger.debug("尝试获取当前用户信息，URL: {}, Token: {}", baseUrl + "/api/user/validate", pureToken);

			HttpEntity<Void> entity = new HttpEntity<>(headers);
			ResponseEntity<Map> response = restTemplate.exchange(
				baseUrl + "/api/user/validate", 
				HttpMethod.GET, 
				entity, 
				Map.class
			);
			
			if (response.getBody() != null) {
				logger.debug("成功获取当前用户信息: {}", response.getBody());
				
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
						return null;
					}
				} else {
					logger.warn("用户服务返回错误: {}", result);
					return null;
				}
			}
		} catch (HttpStatusCodeException ex) {
			logger.warn("获取当前用户信息失败: {} {}", ex.getStatusCode(), ex.getResponseBodyAsString());
		} catch (Exception ex) {
			logger.error("获取当前用户信息时发生异常: {}", ex.getMessage(), ex);
		}
		
		return null;
	}

	/**
	 * 验证token是否有效
	 * @param baseUrl 用户服务基础URL
	 * @param token 认证token
	 * @return 如果token有效返回true，否则返回false
	 */
	public boolean validateToken(String baseUrl, String token) {
		try {
			// 尝试获取当前用户信息来验证token
			Map<String, Object> userInfo = fetchUserById(baseUrl, token, "current");
			return userInfo != null && !userInfo.isEmpty();
		} catch (Exception ex) {
			logger.debug("Token验证失败: {}", ex.getMessage());
			return false;
		}
	}

	/**
	 * 根据用户ID获取用户信息
	 * @param userId 用户ID
	 * @return 用户信息Map，如果失败返回null
	 */
	public Map<String, Object> getUserById(Long userId) {
		try {
			// 使用默认的用户服务地址
			String defaultBaseUrl = "http://localhost:8081";
			return fetchUserById(defaultBaseUrl, "test_token", String.valueOf(userId));
		} catch (Exception ex) {
			logger.debug("获取用户信息失败: {}", ex.getMessage());
			return null;
		}
	}

	/**
	 * 检查用户是否存在
	 * @param userId 用户ID
	 * @return 如果用户存在返回true，否则返回false
	 */
	public boolean userExists(Long userId) {
		try {
			Map<String, Object> userInfo = getUserById(userId);
			return userInfo != null && !userInfo.isEmpty();
		} catch (Exception ex) {
			logger.debug("检查用户是否存在失败: {}", ex.getMessage());
			return false;
		}
	}
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
