package org.example.edusoft.learning.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> fetchUserById(String baseUrl, String token, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        String bearer = token;
        String raw = token;
        if (token != null) {
            if (!token.startsWith("Bearer ")) {
                bearer = "Bearer " + token;
            } else {
                raw = token.substring("Bearer ".length());
            }
            headers.set("Authorization", bearer);
            headers.set("satoken", raw);
            headers.set("Cookie", "satoken=" + raw);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String[] candidates = new String[] {
            baseUrl + "/api/user/" + userId,
            baseUrl + "/api/user/info/" + userId,
            baseUrl + "/user/" + userId
        };
        HttpStatusCodeException lastEx = null;
        for (String url : candidates) {
            try {
                ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
                return resp.getBody();
            } catch (HttpStatusCodeException ex) {
                lastEx = ex;
            }
        }
        if (lastEx != null) {
            throw new IllegalStateException("用户服务请求失败: " + lastEx.getStatusCode() + " " + lastEx.getResponseBodyAsString());
        }
        throw new IllegalStateException("用户服务不可用");
    }
}
