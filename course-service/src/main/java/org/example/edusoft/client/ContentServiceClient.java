package org.example.edusoft.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.List;

/**
 * Content Service客户端接口
 * 用于与content-service进行通信
 */
@FeignClient(name = "content-service", url = "${content.service.url:http://content-service:8083}")
public interface ContentServiceClient {

    /**
     * 获取指定课程的资源总数
     * @param courseId 课程ID
     * @param token 认证令牌
     * @param authorization 授权头
     * @return 包含资源总数的响应
     */
    @GetMapping("/api/resources/count/course/{courseId}")
    Map<String, Object> getResourceCountByCourse(@PathVariable("courseId") Long courseId,
                                               @RequestParam(value = "satoken", required = false) String token,
                                               @RequestParam(value = "Authorization", required = false) String authorization);

    /**
     * 根据课程ID获取作业总数
     * @param courseId 课程ID
     * @param token 认证令牌
     * @param authorization 授权头
     * @return 包含作业总数的响应
     */
    @GetMapping("/api/homework/count/course/{courseId}")
    Map<String, Object> getHomeworkCountByCourse(@PathVariable("courseId") Long courseId,
                                                 @RequestParam(value = "satoken", required = false) String token,
                                                 @RequestParam(value = "Authorization", required = false) String authorization);

}
