package org.example.edusoft.learning.controller;

import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.client.ContentClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/learning/resource")
public class ResourceController {

    @Autowired
    private ContentClient contentClient;

    /**
     * 获取课程资源数量（通过内容服务聚合）
     */
    @GetMapping("/count/course/{courseId}")
    public Result<Integer> getResourceCountByCourse(@PathVariable("courseId") Long courseId) {
        List<Map<String, Object>> list = contentClient.getResourcesByCourseId2(courseId);
        int count = (list == null) ? 0 : list.size();
        return Result.success(count);
    }
}
