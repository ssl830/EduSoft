package org.example.edusoft.learning.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Learning Service 配置类
 */
@Configuration
@ComponentScan(basePackages = "org.example.edusoft.learning.client")
public class LearningServiceConfig {
    
    // 这里可以添加其他配置，比如：
    // - 数据库连接池配置
    // - 缓存配置
    // - 安全配置等
}
