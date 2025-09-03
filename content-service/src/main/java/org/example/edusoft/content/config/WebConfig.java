package org.example.edusoft.content.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 用于注册拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private TokenInterceptor tokenInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册Token拦截器，拦截所有API请求
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/health", "/api/info"); // 排除健康检查等接口
    }
}

