package org.example.edusoft.content.controller;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.entity.discussion.DiscussionLike;
import org.example.edusoft.content.service.discussion.DiscussionLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;

@RestController
@RequestMapping("/api/content/discussion-like")
public class DiscussionLikeController {

    @Autowired
    private DiscussionLikeService discussionLikeService;

    /**
     * 点赞讨论
     * 权限要求：已登录用户
     */
    @PostMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public Result<DiscussionLike> likeDiscussion(@PathVariable Long discussionId) {
        try {
            if (!StpUtil.isLogin()) {
                return Result.error("请先登录");
            }
            
            Long userId = StpUtil.getLoginIdAsLong();
            DiscussionLike like = discussionLikeService.likeDiscussion(discussionId, userId);
            return Result.success(like, "点赞成功");
        } catch (IllegalArgumentException e) {
            return Result.error("点赞失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("点赞失败：" + e.getMessage());
        }
    }

    /**
     * 取消点赞
     * 权限要求：已登录用户
     */
    @DeleteMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public Result<Boolean> unlikeDiscussion(@PathVariable Long discussionId) {
        try {
            if (!StpUtil.isLogin()) {
                return Result.error("请先登录");
            }
            
            Long userId = StpUtil.getLoginIdAsLong();
            discussionLikeService.unlikeDiscussion(discussionId, userId);
            return Result.success(true, "取消点赞成功");
        } catch (Exception e) {
            return Result.error("取消点赞失败：" + e.getMessage());
        }
    }

    /**
     * 获取讨论的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public Result<List<DiscussionLike>> getLikesByDiscussion(@PathVariable Long discussionId) {
        try {
            List<DiscussionLike> likes = discussionLikeService.getLikesByDiscussion(discussionId);
            return Result.success(likes, "获取讨论点赞列表成功");
        } catch (Exception e) {
            return Result.error("获取讨论点赞列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的点赞列表
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public Result<List<DiscussionLike>> getLikesByUser(@PathVariable Long userId) {
        try {
            List<DiscussionLike> likes = discussionLikeService.getLikesByUser(userId);
            return Result.success(likes, "获取用户点赞列表成功");
        } catch (Exception e) {
            return Result.error("获取用户点赞列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取讨论的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/count")
    @SaCheckLogin
    public Result<Integer> countLikesByDiscussion(@PathVariable Long discussionId) {
        try {
            Integer count = discussionLikeService.countLikesByDiscussion(discussionId);
            return Result.success(count, "获取讨论点赞数成功");
        } catch (Exception e) {
            return Result.error("获取讨论点赞数失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的点赞数
     * 权限要求：已登录用户
     */
    @GetMapping("/user/{userId}/count")
    @SaCheckLogin
    public Result<Integer> countLikesByUser(@PathVariable Long userId) {
        try {
            Integer count = discussionLikeService.countLikesByUser(userId);
            return Result.success(count, "获取用户点赞数成功");
        } catch (Exception e) {
            return Result.error("获取用户点赞数失败：" + e.getMessage());
        }
    }

    /**
     * 检查用户是否已点赞
     * 权限要求：已登录用户
     */
    @GetMapping("/discussion/{discussionId}/check")
    @SaCheckLogin
    public Result<Boolean> hasLiked(@PathVariable Long discussionId) {
        try {
            if (!StpUtil.isLogin()) {
                return Result.error("请先登录");
            }
            
            Long userId = StpUtil.getLoginIdAsLong();
            Boolean hasLiked = discussionLikeService.hasLiked(discussionId, userId);
            return Result.success(hasLiked, "检查点赞状态成功");
        } catch (Exception e) {
            return Result.error("检查点赞状态失败：" + e.getMessage());
        }
    }
}  