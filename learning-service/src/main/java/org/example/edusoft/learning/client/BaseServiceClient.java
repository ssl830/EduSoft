package org.example.edusoft.learning.client;

import org.example.edusoft.learning.exception.ServiceCallException2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * 微服务调用基础类
 * 提供统一的调用方式和异常处理
 */
public abstract class BaseServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(BaseServiceClient.class);
    
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
            
            // 获取并传递认证token
            String token = resolveOutboundToken();
            if (token != null && !token.isEmpty()) {
                String pureToken = token.replace("Bearer ", "");
                // 设置satoken头（符合Sa-Token框架）
                headers.set("satoken", pureToken);
                // 同时设置Authorization头作为备选
                headers.set("Authorization", "Bearer " + pureToken);
                logger.debug("[BaseServiceClient] -> {} {} (service={}) token=present", method, url, getServiceName());
            } else {
                logger.warn("[BaseServiceClient] -> {} {} (service={}) token=absent", method, url, getServiceName());
            }
            
            HttpEntity<?> entity = new HttpEntity<>(requestBody, headers);
            logger.debug("[BaseServiceClient] sending request: method={} url={} headers={}", method, url, headers.keySet());
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);
            
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            logger.error("HTTP error calling {}: {} - {}", getServiceName(), ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new ServiceCallException2(
                getServiceName(), 
                ex.getStatusCode().value(), 
                ex.getResponseBodyAsString()
            );
        } catch (Exception ex) {
            logger.error("Error calling {}: {}", getServiceName(), ex.getMessage(), ex);
            throw new ServiceCallException2(getServiceName(), ex.getMessage(), ex);
        }
    }
    
    /**
     * 获取当前请求的认证token
     */
    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }
    
    /**
     * 执行带参数的GET请求，返回Map类型（兼容现有代码）
     */
    protected Map<String, Object> getForMap(String path) {
        return get(path, Map.class);
    }
}
