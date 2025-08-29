package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.LearningProgress;
import org.example.edusoft.content.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content/learning-progress")
@CrossOrigin(origins = "*")
public class LearningProgressController {

    @Autowired
    private LearningProgressService learningProgressService;

    /**
     * 创建学习进度
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProgress(@RequestBody LearningProgress progress) {
        try {
            // 设置创建时间和更新时间
            progress.setCreatedAt(LocalDateTime.now());
            progress.setUpdatedAt(LocalDateTime.now());
            progress.setLastWatchTime(LocalDateTime.now());
            
            LearningProgress created = learningProgressService.createProgress(progress);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "学习进度创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "学习进度创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取进度详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProgress(@PathVariable Long id) {
        try {
            LearningProgress progress = learningProgressService.getProgressById(id);
            Map<String, Object> response = new HashMap<>();
            if (progress != null) {
                response.put("code", 200);
                response.put("msg", "获取成功");
                response.put("data", progress);
            } else {
                response.put("code", 404);
                response.put("msg", "学习进度不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取学习进度失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取资源学生进度
     */
    @GetMapping("/resource/{resourceId}/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getProgressByResourceAndStudent(
            @PathVariable Long resourceId, 
            @PathVariable Long studentId) {
        try {
            LearningProgress progress = learningProgressService.getProgressByResourceAndStudent(resourceId, studentId);
            Map<String, Object> response = new HashMap<>();
            if (progress != null) {
                response.put("code", 200);
                response.put("msg", "获取成功");
                response.put("data", progress);
            } else {
                response.put("code", 404);
                response.put("msg", "学习进度不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取学习进度失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取学生进度列表
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getProgressByStudent(@PathVariable Long studentId) {
        try {
            List<LearningProgress> progressList = learningProgressService.getProgressByStudentId(studentId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", progressList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取学习进度列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取资源进度列表
     */
    @GetMapping("/resource/{resourceId}")
    public ResponseEntity<Map<String, Object>> getProgressByResource(@PathVariable Long resourceId) {
        try {
            List<LearningProgress> progressList = learningProgressService.getProgressByResourceId(resourceId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", progressList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取学习进度列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新进度
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProgress(@PathVariable Long id, @RequestBody LearningProgress progress) {
        try {
            progress.setId(id);
            progress.setUpdatedAt(LocalDateTime.now());
            progress.setLastWatchTime(LocalDateTime.now());
            
            LearningProgress updated = learningProgressService.updateProgress(progress);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "学习进度更新成功");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "学习进度更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新进度（通过资源ID和学生ID）
     */
    @PutMapping("/resource/{resourceId}/student/{studentId}")
    public ResponseEntity<Map<String, Object>> updateProgressByResourceAndStudent(
            @PathVariable Long resourceId,
            @PathVariable Long studentId,
            @RequestParam(required = false) Integer progress,
            @RequestParam(required = false) Integer lastPosition) {
        try {
            learningProgressService.updateProgress(resourceId, studentId, progress, lastPosition);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "学习进度更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "学习进度更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 增加观看次数
     */
    @PutMapping("/resource/{resourceId}/student/{studentId}/watch")
    public ResponseEntity<Map<String, Object>> incrementWatchCount(
            @PathVariable Long resourceId,
            @PathVariable Long studentId) {
        try {
            learningProgressService.incrementWatchCount(resourceId, studentId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "观看次数更新成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "观看次数更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}
