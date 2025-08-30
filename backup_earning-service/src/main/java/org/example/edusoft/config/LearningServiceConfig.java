package org.example.edusoft.learning.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "org.example.edusoft.learning.client")
public class LearningServiceConfig {
    // 可扩展配置
}
