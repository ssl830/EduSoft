package com.bugvictims.demo11.Config;

import com.bugvictims.demo11.Interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        //拦截所有请求，除了注册和登录
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns("/user/register", "/user/login");
    }

}
