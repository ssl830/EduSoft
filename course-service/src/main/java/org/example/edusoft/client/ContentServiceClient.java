package org.example.edusoft.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Content Service客户端接口
 * 用于与content-service进行通信
 */
@FeignClient(name = "content-service", url = "${content.service.url:http://localhost:8083}")
public interface ContentServiceClient {

    /**
     * 根据课程ID获取作业总数
     * @param courseId 课程ID
     * @return 包含作业总数的响应
     */
    @GetMapping("/api/content/homework/count/course/{courseId}")
    Map<String, Object> getHomeworkCountByCourse(@PathVariable("courseId") Long courseId);
}
