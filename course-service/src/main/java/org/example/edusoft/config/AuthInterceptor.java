package org.example.edusoft.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.edusoft.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	
	private final UserServiceClient userServiceClient;

	@Value("${services.user.base-url:http://localhost:8081}")
	private String userServiceBaseUrl;

	@Value("${auth.required:true}")
	private boolean authRequired;

	public AuthInterceptor(UserServiceClient userServiceClient) {
		this.userServiceClient = userServiceClient;
	}

	/**
	 * 从请求中提取认证token
	 * 优先级：satoken > Authorization > Cookie
	 */
	private String extractToken(HttpServletRequest request) {
		// 优先使用satoken头（符合Sa-Token框架）
		String satoken = request.getHeader("satoken");
		if (satoken != null && !satoken.trim().isEmpty()) {
			logger.info("从satoken头获取token: {}", satoken);
			return satoken;
		}

		// 其次使用Authorization头
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && !authHeader.trim().isEmpty()) {
			logger.info("从Authorization头获取token: {}", authHeader);
			return authHeader;
		}

		// 最后尝试从Cookie中获取
		String cookie = request.getHeader("Cookie");
		if (cookie != null) {
			for (String part : cookie.split(";")) {
				String p = part.trim();
				if (p.startsWith("satoken=")) {
					String token = p.substring("satoken=".length());
					logger.info("从Cookie获取token: {}", token);
					return token;
				}
			}
		}

		logger.warn("未找到有效的认证token");
		return null;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String requestPath = request.getRequestURI();
		logger.debug("拦截请求: {} {}", request.getMethod(), requestPath);

		// 支持通过配置或请求头绕过鉴权，便于联调/测试
		if (!authRequired) {
			logger.debug("鉴权已禁用，直接放行");
			return true;
		}
		
		String bypass = request.getHeader("X-Bypass-Auth");
		if (bypass != null && ("true".equalsIgnoreCase(bypass) || "1".equals(bypass))) {
			logger.debug("通过X-Bypass-Auth头绕过鉴权");
			return true;
		}

		// 提取认证token
		String token = extractToken(request);
		
		if (token == null || token.trim().isEmpty()) {
			logger.warn("请求缺少认证token: {}", requestPath);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		try {
			// 通过token获取当前用户信息
			logger.debug("验证token: {}", token);
			Map<String, Object> user = userServiceClient.fetchCurrentUser(userServiceBaseUrl, token);
			
			if (user == null || user.isEmpty()) {
				logger.warn("token验证失败，用户信息为空");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}

			// 验证用户信息完整性
			Object userId = user.get("id");
			Object username = user.get("username");
			
			logger.debug("获取到用户信息: id={}, username={}, 完整信息={}", userId, username, user);
			
			if (userId == null) {
				logger.warn("用户信息中缺少id字段: {}", user);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}

			// 将用户信息存储到请求属性中，供后续使用
			request.setAttribute("currentUser", user);
			logger.debug("用户验证成功: id={}, username={}", userId, username);
			return true;
			
		} catch (Exception ex) {
			logger.error("用户验证过程中发生异常: {}", ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}
}
