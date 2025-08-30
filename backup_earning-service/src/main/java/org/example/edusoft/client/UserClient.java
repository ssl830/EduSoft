package org.example.edusoft.learning.client;

import org.example.edusoft.common.domain.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {
    @GetMapping("/user/getUserName")
    Result<String> getUserName(@RequestParam("userId") Long userId);
}
