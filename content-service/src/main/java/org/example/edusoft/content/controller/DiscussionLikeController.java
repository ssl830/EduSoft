package org.example.edusoft.content.controller;
import org.example.edusoft.content.entity.discussion.DiscussionLike;
import org.example.edusoft.content.service.discussion.DiscussionLikeService;
import org.example.edusoft.content.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/discussion-like")
public class DiscussionLikeController {

    @Autowired
    private DiscussionLikeService discussionLikeService;
    
    @Autowired
    private UserClient userClient;

    /**
     * 点赞讨论
     * 权限要求：已登录用户
     */
    @PostMapping("/discussion/{discussionId}")
    public ResponseEntity<?> likeDiscussion(@PathVariable Long discussionId, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        try {
            boolean validate = userClient.validateToken("http://localhost:8081", token);
            if (validate == false) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            String userId = userData.get("userId").toString();
            DiscussionLike like = discussionLikeService.likeDiscussion(discussionId, userId);
            return ResponseEntity.ok(like);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "点赞失败：" + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * 取消点赞
     * 权限要求：已登录用户
     */
    @DeleteMapping("/discussion/{discussionId}")
    public ResponseEntity<?> unlikeDiscussion(@PathVariable Long discussionId, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        try {
            boolean validate = userClient.validateToken("http://localhost:8081", token);
            if (validate == false) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            String userId = userData.get("userId").toString();
            discussionLikeService.unlikeDiscussion(discussionId, userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "取消点赞成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "取消点赞失败：" + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * 获取讨论的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}")
    public ResponseEntity<List<DiscussionLike>> getLikesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionLikeService.getLikesByDiscussion(discussionId));
    }

    /**
     * 获取用户的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DiscussionLike>> getLikesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(discussionLikeService.getLikesByUser(userId));
    }

    /**
     * 获取讨论的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/count")
    public ResponseEntity<Integer> countLikesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionLikeService.countLikesByDiscussion(discussionId));
    }

    /**
     * 获取用户的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> countLikesByUser(@PathVariable String userId) {
        return ResponseEntity.ok(discussionLikeService.countLikesByUser(userId));
    }

    /**
     * 检查用户是否已点赞
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/check")
    public ResponseEntity<?> hasLiked(@PathVariable Long discussionId, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        try {
            boolean validate = userClient.validateToken("http://localhost:8081", token);
            if (validate == false) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "登录状态无效");
                return ResponseEntity.status(401).body(response);
            }
            Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
            String userId = userData.get("userId").toString();
            Boolean hasLiked = discussionLikeService.hasLiked(discussionId, userId);
            return ResponseEntity.ok(hasLiked);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "检查点赞状态失败：" + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}