package org.example.edusoft.learning.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 微服务间调用的认证拦截器
 * 用于自动传递用户认证信息
 */
public class AuthenticationInterceptor implements ClientHttpRequestInterceptor {
    
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, 
            byte[] body, 
            ClientHttpRequestExecution execution) throws IOException {
        
        // 获取当前请求的认证信息
        ServletRequestAttributes attributes = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (attributes != null) {
            HttpServletRequest httpRequest = attributes.getRequest();
            
            // 传递Authorization头
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader != null) {
                request.getHeaders().set("Authorization", authHeader);
            }
            
            // 传递satoken
            String satoken = httpRequest.getHeader("satoken");
            if (satoken != null) {
                request.getHeaders().set("satoken", satoken);
            }
            
            // 传递Cookie中的satoken
            String cookie = httpRequest.getHeader("Cookie");
            if (cookie != null) {
                request.getHeaders().set("Cookie", cookie);
            }
        }
        
        return execution.execute(request, body);
    }
}
