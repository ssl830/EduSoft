//package org.example.edusoft.learning.client;
//
//import org.example.edusoft.common.Result;
//import org.example.edusoft.common.dto.CourseDTO;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@FeignClient(name = "course-service")
//public interface CourseClient {
//
//    @GetMapping("/api/course/{id}")
//    Result<CourseDTO> getCourseById(@PathVariable("id") Long id);
//}
