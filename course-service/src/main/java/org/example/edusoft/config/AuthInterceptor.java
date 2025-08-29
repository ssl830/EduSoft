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
	 * 优先级：Authorization > satoken > Cookie
	 */
	private String extractToken(HttpServletRequest request) {
		// 优先使用Authorization头
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && !authHeader.trim().isEmpty()) {
			logger.debug("从Authorization头获取token: {}", authHeader);
			return authHeader;
		}

		// 其次使用satoken头
		String satoken = request.getHeader("satoken");
		if (satoken != null && !satoken.trim().isEmpty()) {
			logger.debug("从satoken头获取token: {}", satoken);
			return satoken;
		}

		// 最后尝试从Cookie中获取
		String cookie = request.getHeader("Cookie");
		if (cookie != null) {
			for (String part : cookie.split(";")) {
				String p = part.trim();
				if (p.startsWith("satoken=")) {
					String token = p.substring("satoken=".length());
					logger.debug("从Cookie获取token: {}", token);
					return token;
				}
			}
		}

		logger.debug("未找到有效的认证token");
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

		// 提取认证信息
		String token = extractToken(request);
		String userId = request.getHeader("X-User-Id");
		
		if (userId == null || userId.trim().isEmpty()) {
			logger.warn("请求缺少X-User-Id头: {}", requestPath);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		
		if (token == null || token.trim().isEmpty()) {
			logger.warn("请求缺少认证token: {}", requestPath);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}

		try {
			logger.debug("验证用户: {}, Token: {}", userId, token);
			Map<String, Object> user = userServiceClient.fetchUserById(userServiceBaseUrl, token, userId);
			
			if (user == null || user.isEmpty()) {
				logger.warn("用户验证失败，用户ID: {}", userId);
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}

			// 将用户信息存储到请求属性中，供后续使用
			request.setAttribute("currentUser", user);
			logger.debug("用户验证成功: {}", userId);
			return true;
			
		} catch (Exception ex) {
			logger.error("用户验证过程中发生异常: {}", ex.getMessage(), ex);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}
}
