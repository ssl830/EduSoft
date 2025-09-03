package org.example.edusoft.content.controller;

import org.example.edusoft.content.dto.reply.CreateReplyRequest;
import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.service.reply.DiscussionReplyService;
import org.example.edusoft.content.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 讨论回复管理控制器
 */
@RestController
@RequestMapping("/api/discussion-reply")
@CrossOrigin(origins = "*")
public class DiscussionReplyController {

    @Autowired
    private DiscussionReplyService discussionReplyService;
    
    @Autowired
    private UserClient userClient;

    /**
     * 创建回复
     */
    @PostMapping
    public ResponseEntity<?> createReply(@Valid @RequestBody CreateReplyRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "请先登录");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            boolean isValid = userClient.validateToken("http://localhost:8081", token);
            if (!isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            if (userData == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Long creatorId = ((Number) userData.get("id")).longValue();
            // user-service 校验返回中，用户的学号/工号字段为 userId（与 DiscussionController 一致用作 creator_num）
            String userNum = (String) userData.get("userId");
            DiscussionReply reply = discussionReplyService.createReply(
                request.getDiscussionId(),
                request.getParentReplyId(),
                creatorId,
                request.getContent(),
                userNum
            );
            return ResponseEntity.ok(reply);
        } catch (IllegalArgumentException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resp);
        } catch (Exception e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "回复创建失败: " + e.getMessage());
            return ResponseEntity.status(400).body(resp);
        }
    }

    /**
     * 获取回复详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getReply(@PathVariable Long id) {
        DiscussionReply reply = discussionReplyService.getReply(id);
        if (reply != null) {
            return ResponseEntity.ok(reply);
        } else {
            return ResponseEntity.status(404).body(Map.of("error", "回复不存在"));
        }
    }

    /**
     * 获取讨论的所有回复
     */
    @GetMapping("/discussion/{discussionId}")
    public ResponseEntity<List<DiscussionReplyDTO>> getDiscussionReplies(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionReplyService.getDiscussionReplies(discussionId));
    }

    /**
     * 获取回复的子回复
    */
    @GetMapping("/{id}/children")
    public ResponseEntity<List<DiscussionReplyDTO>> getChildReplies(@PathVariable Long id) {
        return ResponseEntity.ok(discussionReplyService.getChildReplies(id));
    }

    /**
     * 更新回复
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReply(@PathVariable Long id, @RequestBody String content, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "请先登录");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            boolean isValid = userClient.validateToken("http://localhost:8081", token);
            if (!isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            if (userData == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Long userId = ((Number) userData.get("id")).longValue();
            DiscussionReply reply = discussionReplyService.updateReply(id, content);
            if (reply != null) {
                return ResponseEntity.ok(reply);
            } else {
                return ResponseEntity.status(404).body(Map.of("error", "回复不存在"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "更新回复失败: " + e.getMessage()));
        }
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReply(@PathVariable Long id, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "请先登录");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            boolean isValid = userClient.validateToken("http://localhost:8081", token);
            if (!isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            if (userData == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            boolean success = discussionReplyService.deleteReply(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "回复删除成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "回复删除失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "回复删除失败: " + e.getMessage()));
        }
    }

    /**
     * 点赞回复
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeReply(@PathVariable Long id, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "请先登录");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            boolean isValid = userClient.validateToken("http://localhost:8081", token);
            if (!isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            if (userData == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Long userId = ((Number) userData.get("id")).longValue();
            boolean success = discussionReplyService.likeReply(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "点赞成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "点赞失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "点赞失败: " + e.getMessage()));
        }
    }

    /**
     * 取消点赞
     */
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlikeReply(@PathVariable Long id, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "请先登录");
            return ResponseEntity.status(401).body(resp);
        }
        try {
            boolean isValid = userClient.validateToken("http://localhost:8081", token);
            if (!isValid) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            if (userData == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Long userId = ((Number) userData.get("id")).longValue();
            boolean success = discussionReplyService.unlikeReply(id);
            if (success) {
                return ResponseEntity.ok(Map.of("message", "取消点赞成功"));
            } else {
                return ResponseEntity.status(400).body(Map.of("error", "取消点赞失败"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of("error", "取消点赞失败: " + e.getMessage()));
        }
    }

    /**
     * 获取用户的回复列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiscussionReplyDTO>> getUserReplies(@PathVariable Long userId) {
        return ResponseEntity.ok(discussionReplyService.getUserReplies(userId));
    }

    /**
     * 搜索回复
     */
    @GetMapping("/search")
    public ResponseEntity<List<DiscussionReplyDTO>> searchReplies(@RequestParam String keyword) {
        return ResponseEntity.ok(discussionReplyService.searchReplies(keyword));
    }
}

