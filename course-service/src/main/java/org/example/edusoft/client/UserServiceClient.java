package org.example.edusoft.client;

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
public class UserServiceClient {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceClient.class);
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
				
				// 优先使用satoken头（符合Sa-Token框架）
				headers.set("satoken", pureToken);
				
				// 同时设置Authorization头作为备选
				headers.set("Authorization", "Bearer " + pureToken);
				
				logger.debug("尝试请求用户服务，URL: {}, UserId: {}, Token: {}", url, userId, pureToken);

				HttpEntity<Void> entity = new HttpEntity<>(headers);
				ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
				
				if (response.getBody() != null) {
					logger.debug("成功获取用户信息: {}", response.getBody());
					return response.getBody();
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
			logger.error("用户服务请求失败，状态码: {}, 响应: {}", 
				lastEx.getStatusCode(), lastEx.getResponseBodyAsString());
		} else {
			logger.error("用户服务不可用，所有端点都无法访问");
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
}
