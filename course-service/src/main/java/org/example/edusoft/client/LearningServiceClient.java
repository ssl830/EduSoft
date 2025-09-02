package org.example.edusoft.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "learning-service", url = "${learning.service.url:http://localhost:8084}")
public interface LearningServiceClient {

    // learning-service 返回的是 Result 包裹
    @GetMapping("/api/practice/course/{courseId}")
    Map<String, Object> getCoursePractices(@PathVariable("courseId") Long courseId,
                                           @RequestHeader(value = "satoken", required = false) String token,
                                           @RequestHeader(value = "Authorization", required = false) String authorization);

    // 资源数量统计（learning-service聚合）
    @GetMapping("/api/learning/resource/count/course/{courseId}")
    Map<String, Object> getResourceCountByCourse(@PathVariable("courseId") Long courseId,
                                                 @RequestHeader(value = "satoken", required = false) String token,
                                                 @RequestHeader(value = "Authorization", required = false) String authorization);
}
