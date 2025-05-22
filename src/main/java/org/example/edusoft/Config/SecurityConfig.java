package org.example.edusoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // 关闭 CSRF
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 所有请求都放行
            )
            .httpBasic(httpBasic -> httpBasic.disable()); // 关闭 Basic 登录框

        return http.build();
    }
}


