package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.DiscussionReply;
import org.example.edusoft.content.service.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/content/discussion-reply")
@CrossOrigin(origins = "*")
public class DiscussionReplyController {

    @Autowired
    private DiscussionReplyService discussionReplyService;

    /**
     * 创建回复
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createReply(@RequestBody DiscussionReply reply) {
        try {
            DiscussionReply created = discussionReplyService.createReply(reply);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "回复创建成功");
            response.put("data", created);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "回复创建失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据ID获取回复
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getReply(@PathVariable Long id) {
        try {
            DiscussionReply reply = discussionReplyService.getReplyById(id);
            Map<String, Object> response = new HashMap<>();
            if (reply != null) {
                response.put("code", 200);
                response.put("msg", "获取成功");
                response.put("data", reply);
            } else {
                response.put("code", 404);
                response.put("msg", "回复不存在");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取回复失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据讨论ID获取回复列表
     */
    @GetMapping("/discussion/{discussionId}")
    public ResponseEntity<Map<String, Object>> getRepliesByDiscussion(@PathVariable Long discussionId) {
        try {
            List<DiscussionReply> replies = discussionReplyService.getRepliesByDiscussionId(discussionId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", replies);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取回复列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据用户ID获取回复列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getRepliesByUser(@PathVariable Long userId) {
        try {
            List<DiscussionReply> replies = discussionReplyService.getRepliesByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", replies);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取回复列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据父回复ID获取回复列表
     */
    @GetMapping("/parent/{parentReplyId}")
    public ResponseEntity<Map<String, Object>> getRepliesByParent(@PathVariable Long parentReplyId) {
        try {
            List<DiscussionReply> replies = discussionReplyService.getRepliesByParentReplyId(parentReplyId);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "获取成功");
            response.put("data", replies);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "获取回复列表失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 更新回复
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateReply(@PathVariable Long id, @RequestBody DiscussionReply reply) {
        try {
            reply.setId(id);
            DiscussionReply updated = discussionReplyService.updateReply(reply);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "回复更新成功");
            response.put("data", updated);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "回复更新失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteReply(@PathVariable Long id) {
        try {
            discussionReplyService.deleteReply(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "回复删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "回复删除失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 标记为教师回复
     */
    @PutMapping("/{id}/teacher")
    public ResponseEntity<Map<String, Object>> markAsTeacherReply(@PathVariable Long id) {
        try {
            discussionReplyService.markAsTeacherReply(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", 200);
            response.put("msg", "已标记为教师回复");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 500);
            response.put("msg", "标记失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

