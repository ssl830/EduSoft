package org.example.edusoft.controller.discussion;

import org.example.edusoft.entity.discussion.DiscussionLike;
import org.example.edusoft.service.discussion.DiscussionLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;

@RestController
@RequestMapping("/api/discussion-like")
public class DiscussionLikeController {

    @Autowired
    private DiscussionLikeService discussionLikeService;

    /**
     * 点赞讨论
     * 权限要求：已登录用户
     */
    @PostMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public ResponseEntity<?> likeDiscussion(@PathVariable Long discussionId) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        
        try {
            Long userId = StpUtil.getLoginIdAsLong();
            DiscussionLike like = discussionLikeService.likeDiscussion(discussionId, userId);
            return ResponseEntity.ok(like);
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 取消点赞
     * 权限要求：已登录用户
     */
    @DeleteMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public ResponseEntity<?> unlikeDiscussion(@PathVariable Long discussionId) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        
        Long userId = StpUtil.getLoginIdAsLong();
        discussionLikeService.unlikeDiscussion(discussionId, userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "取消点赞成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取讨论的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionLike>> getLikesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionLikeService.getLikesByDiscussion(discussionId));
    }

    /**
     * 获取用户的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionLike>> getLikesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(discussionLikeService.getLikesByUser(userId));
    }

    /**
     * 获取讨论的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countLikesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionLikeService.countLikesByDiscussion(discussionId));
    }

    /**
     * 获取用户的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countLikesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(discussionLikeService.countLikesByUser(userId));
    }

    /**
     * 检查用户是否已点赞
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/check")
    @SaCheckLogin
    public ResponseEntity<Boolean> hasLiked(@PathVariable Long discussionId) {
        Long userId = StpUtil.getLoginIdAsLong();
        return ResponseEntity.ok(discussionLikeService.hasLiked(discussionId, userId));
    }
} 