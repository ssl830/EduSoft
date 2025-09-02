package org.example.edusoft.content.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
public class OssConfig {
    
    @Value("${aliyun.oss.endpoint:oss-cn-hangzhou.aliyuncs.com}")
    private String endpoint;
    
    @Value("${aliyun.oss.accessKeyId:test-access-key-id}")
    private String accessKeyId;
    
    @Value("${aliyun.oss.accessKeySecret:test-access-key-secret}")
    private String accessKeySecret;
    
    @Value("${aliyun.oss.bucketName:test-bucket-name}")
    private String bucketName;
    
    // 暂时注释掉OSS客户端，避免启动失败
    // @Bean
    // public OSS ossClient() {
    //     return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    // }
    
    public String getBucketName() {
        return bucketName;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
}
