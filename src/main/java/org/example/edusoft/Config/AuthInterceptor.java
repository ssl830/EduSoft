package org.example.edusoft.config;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 检查是否已登录
        if (StpUtil.isLogin()) {
            // 获取用户ID并设置到请求属性中
            Long userId = StpUtil.getLoginIdAsLong();
            request.setAttribute("userId", userId);
            return true;
        }
        return false;
    }
} 