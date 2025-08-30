package org.example.edusoft.learning.client;

import org.example.edusoft.learning.exception.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 微服务调用基础类
 * 提供统一的调用方式和异常处理
 */
public abstract class BaseServiceClient {
    
    @Autowired
    protected RestTemplate restTemplate;
    
    /**
     * 获取服务名称
     */
    protected abstract String getServiceName();
    
    /**
     * 获取服务基础URL
     */
    protected abstract String getBaseUrl();
    
    /**
     * 执行GET请求
     */
    protected <T> T get(String path, Class<T> responseType) {
        return execute(path, HttpMethod.GET, null, responseType);
    }
    
    /**
     * 执行POST请求
     */
    protected <T> T post(String path, Object requestBody, Class<T> responseType) {
        return execute(path, HttpMethod.POST, requestBody, responseType);
    }
    
    /**
     * 执行PUT请求
     */
    protected <T> T put(String path, Object requestBody, Class<T> responseType) {
        return execute(path, HttpMethod.PUT, requestBody, responseType);
    }
    
    /**
     * 执行DELETE请求
     */
    protected <T> T delete(String path, Class<T> responseType) {
        return execute(path, HttpMethod.DELETE, null, responseType);
    }
    
    /**
     * 通用的HTTP请求执行方法
     */
    protected <T> T execute(String path, HttpMethod method, Object requestBody, Class<T> responseType) {
        String url = getBaseUrl() + path;
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            
            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            throw new ServiceCallException(
                getServiceName(), 
                ex.getStatusCode().value(), 
                ex.getResponseBodyAsString()
            );
        } catch (Exception ex) {
            throw new ServiceCallException(getServiceName(), ex.getMessage(), ex);
        }
    }
    
    /**
     * 执行带参数的GET请求，返回Map类型（兼容现有代码）
     */
    protected Map<String, Object> getForMap(String path) {
        return get(path, Map.class);
    }
}
