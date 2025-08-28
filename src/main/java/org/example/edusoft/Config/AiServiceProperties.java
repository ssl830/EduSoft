package org.example.edusoft.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.service")
public class AiServiceProperties {
    private String url;
    private String baseUrl;
    private Integer connectTimeout;
    private Integer readTimeout;
    private Integer maxRetries;
    private String apiKey;
    private String allowedFileTypes;
    private String maxFileSize;
} 