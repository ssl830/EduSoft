package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.Discussion;
import org.example.edusoft.content.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content/discussion")
@CrossOrigin(origins = "*")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;

    /**
     * 创建讨论
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDiscussion(@RequestBody Discussion discussion) {
        try {
            Discussion created = discussionService.createDiscussion(discussion);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "讨论创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "讨论创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据ID获取讨论
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDiscussion(@PathVariable Long id) {
        try {
            Discussion discussion = discussionService.getDiscussionById(id);
            Map<String, Object> response = new HashMap<>();
            if (discussion != null) {
                response.put("code", 200);
                response.put("msg", "获取成功");
                response.put("data", discussion);
            } else {
                response.put("code", 404);
                response.put("msg", "讨论不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据创建者ID获取讨论列表
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<Map<String, Object>> getDiscussionsByCreator(@PathVariable Long creatorId) {
        try {
            List<Discussion> discussions = discussionService.getDiscussionsByCreatorId(creatorId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", discussions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据课程ID获取讨论列表
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<Map<String, Object>> getDiscussionsByCourse(@PathVariable Long courseId) {
        try {
            List<Discussion> discussions = discussionService.getDiscussionsByCourseId(courseId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", discussions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据班级ID获取讨论列表
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<Map<String, Object>> getDiscussionsByClass(@PathVariable Long classId) {
        try {
            List<Discussion> discussions = discussionService.getDiscussionsByClassId(classId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", discussions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据类型获取讨论列表
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getDiscussionsByType(@PathVariable String type) {
        try {
            List<Discussion> discussions = discussionService.getDiscussionsByType(type);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", discussions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 获取所有讨论
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDiscussions() {
        try {
            List<Discussion> discussions = discussionService.getAllDiscussions();
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", discussions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取讨论列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新讨论
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDiscussion(@PathVariable Long id, @RequestBody Discussion discussion) {
        try {
            discussion.setId(id);
            Discussion updated = discussionService.updateDiscussion(discussion);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "讨论更新成功");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "讨论更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 关闭讨论
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeDiscussion(@PathVariable Long id) {
        try {
            discussionService.closeDiscussion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "讨论已关闭");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "关闭讨论失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 置顶讨论
     */
    @PutMapping("/{id}/pin")
    public ResponseEntity<Map<String, Object>> pinDiscussion(@PathVariable Long id) {
        try {
            discussionService.pinDiscussion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "讨论已置顶");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "置顶讨论失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 取消置顶讨论
     */
    @PutMapping("/{id}/unpin")
    public ResponseEntity<Map<String, Object>> unpinDiscussion(@PathVariable Long id) {
        try {
            discussionService.unpinDiscussion(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "讨论已取消置顶");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "取消置顶失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 增加浏览次数
     */
    @PutMapping("/{id}/view")
    public ResponseEntity<Map<String, Object>> incrementViewCount(@PathVariable Long id) {
        try {
            discussionService.incrementViewCount(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "浏览次数已更新");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "更新浏览次数失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 增加回复次数
     */
    @PutMapping("/{id}/reply")
    public ResponseEntity<Map<String, Object>> incrementReplyCount(@PathVariable Long id) {
        try {
            discussionService.incrementReplyCount(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "回复次数已更新");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "更新回复次数失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

