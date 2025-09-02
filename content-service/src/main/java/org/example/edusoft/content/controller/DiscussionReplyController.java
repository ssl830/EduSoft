package org.example.edusoft.content.controller;

import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.dto.reply.CreateReplyRequest;
import org.example.edusoft.content.dto.reply.DiscussionReplyDTO;
import org.example.edusoft.content.entity.reply.DiscussionReply;
import org.example.edusoft.content.service.reply.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

/**
 * 讨论回复管理控制器
 */
@RestController
@RequestMapping("/api/content/discussion-replies")
@CrossOrigin(origins = "*")
public class DiscussionReplyController {

    @Autowired
    private DiscussionReplyService discussionReplyService;

    /**
     * 创建回复
     */
    @PostMapping
    public Result<DiscussionReply> createReply(@Valid @RequestBody CreateReplyRequest request) {
        try {
            // 获取当前登录用户ID
            Long creatorId = 1L; // TODO: 从认证信息获取
            DiscussionReply reply = discussionReplyService.createReply(
                request.getDiscussionId(), 
                request.getParentReplyId(), 
                creatorId, 
                request.getContent()
            );
            return Result.success(reply, "回复创建成功");
        } catch (Exception e) {
            return Result.error("回复创建失败: " + e.getMessage());
        }
    }

    /**
     * 获取回复详情
     */
    @GetMapping("/{id}")
    public Result<DiscussionReply> getReply(@PathVariable Long id) {
        try {
            DiscussionReply reply = discussionReplyService.getReply(id);
            if (reply != null) {
                return Result.success(reply, "获取回复成功");
            } else {
                return Result.error("回复不存在");
            }
        } catch (Exception e) {
            return Result.error("获取回复失败: " + e.getMessage());
        }
    }

    /**
     * 获取讨论的所有回复
     */
    @GetMapping("/discussion/{discussionId}")
    public Result<List<DiscussionReplyDTO>> getDiscussionReplies(@PathVariable Long discussionId) {
        try {
            List<DiscussionReplyDTO> replies = discussionReplyService.getDiscussionReplies(discussionId);
            return Result.success(replies, "获取回复列表成功");
        } catch (Exception e) {
            return Result.error("获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取回复的子回复
     */
    @GetMapping("/{id}/children")
    public Result<List<DiscussionReplyDTO>> getChildReplies(@PathVariable Long id) {
        try {
            List<DiscussionReplyDTO> replies = discussionReplyService.getChildReplies(id);
            return Result.success(replies, "获取子回复成功");
        } catch (Exception e) {
            return Result.error("获取子回复失败: " + e.getMessage());
        }
    }

    /**
     * 更新回复
     */
    @PutMapping("/{id}")
    public Result<DiscussionReply> updateReply(@PathVariable Long id, @RequestBody String content) {
        try {
            DiscussionReply reply = discussionReplyService.updateReply(id, content);
            if (reply != null) {
                return Result.success(reply, "回复更新成功");
            } else {
                return Result.error("回复不存在");
            }
        } catch (Exception e) {
            return Result.error("回复更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除回复
     */
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteReply(@PathVariable Long id) {
        try {
            boolean success = discussionReplyService.deleteReply(id);
            if (success) {
                return Result.success(true, "回复删除成功");
            } else {
                return Result.error("回复删除失败");
            }
        } catch (Exception e) {
            return Result.error("回复删除失败: " + e.getMessage());
        }
    }

    /**
     * 点赞回复
     */
    @PostMapping("/{id}/like")
    public Result<Boolean> likeReply(@PathVariable Long id) {
        try {
            boolean success = discussionReplyService.likeReply(id);
            if (success) {
                return Result.success(true, "点赞成功");
            } else {
                return Result.error("点赞失败");
            }
        } catch (Exception e) {
            return Result.error("点赞失败: " + e.getMessage());
        }
    }

    /**
     * 取消点赞
     */
    @PostMapping("/{id}/unlike")
    public Result<Boolean> unlikeReply(@PathVariable Long id) {
        try {
            boolean success = discussionReplyService.unlikeReply(id);
            if (success) {
                return Result.success(true, "取消点赞成功");
            } else {
                return Result.error("取消点赞失败");
            }
        } catch (Exception e) {
            return Result.error("取消点赞失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的回复列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<DiscussionReplyDTO>> getUserReplies(@PathVariable Long userId) {
        try {
            List<DiscussionReplyDTO> replies = discussionReplyService.getUserReplies(userId);
            return Result.success(replies, "获取用户回复列表成功");
        } catch (Exception e) {
            return Result.error("获取用户回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 搜索回复
     */
    @GetMapping("/search")
    public Result<List<DiscussionReplyDTO>> searchReplies(@RequestParam String keyword) {
        try {
            List<DiscussionReplyDTO> replies = discussionReplyService.searchReplies(keyword);
            return Result.success(replies, "搜索回复成功");
        } catch (Exception e) {
            return Result.error("搜索回复失败: " + e.getMessage());
        }
    }
}

