package org.example.edusoft.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.edusoft.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	private final UserServiceClient userServiceClient;

	@Value("${services.user.base-url:http://localhost:8081}")
	private String userServiceBaseUrl;

	@Value("${auth.required:true}")
	private boolean authRequired;

	public AuthInterceptor(UserServiceClient userServiceClient) {
		this.userServiceClient = userServiceClient;
	}

	private String resolveToken(HttpServletRequest request) {
		String satoken = request.getHeader("satoken");
		if (satoken != null && !satoken.isEmpty()) return satoken;
		String cookie = request.getHeader("Cookie");
		if (cookie != null) {
			for (String part : cookie.split(";")) {
				String p = part.trim();
				if (p.startsWith("satoken=")) {
					return p.substring("satoken=".length());
				}
			}
		}
		return request.getHeader("Authorization");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		// 支持通过配置或请求头绕过鉴权，便于联调/测试
		if (!authRequired) {
			return true;
		}
		String bypass = request.getHeader("X-Bypass-Auth");
		if (bypass != null && ("true".equalsIgnoreCase(bypass) || "1".equals(bypass))) {
			return true;
		}

		String tokenOrBearer = resolveToken(request);
		String userId = request.getHeader("X-User-Id");
		if (userId == null || userId.isEmpty() || tokenOrBearer == null || tokenOrBearer.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		try {
			Map<String, Object> user = userServiceClient.fetchUserById(userServiceBaseUrl, tokenOrBearer, userId);
			if (user == null || user.isEmpty()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return false;
			}
			request.setAttribute("X-User", user);
			return true;
		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
	}
}
