package org.example.edusoft.controller.discussion;
import org.example.edusoft.entity.discussion.DiscussionReply;
import org.example.edusoft.service.discussion.DiscussionReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import org.example.edusoft.service.user.UserService;
import java.util.HashMap;
import java.util.Map;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.service.discussion.DiscussionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/discussion-reply")
public class DiscussionReplyController {

    private static final Logger logger = LoggerFactory.getLogger(DiscussionReplyController.class);

    @Autowired
    private DiscussionReplyService discussionReplyService;
    @Autowired
    private UserService userService;
    @Autowired
    private DiscussionService discussionService;
    /**
     * 创建回复
     * 权限要求：已登录用户
     * 功能说明：创建新的回复，自动设置创建者ID为当前登录用户
     * @param discussionId 讨论ID
     * @param parentReplyId 父回复ID（可选）
     * @param reply 回复内容
     */
    @PostMapping("/discussion/{discussionId}")
    public ResponseEntity<?> createReply(
            @PathVariable Long discussionId,
            @RequestParam(required = false) Long parentReplyId,
            @RequestBody DiscussionReply reply) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        // 获取当前登录用户信息
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(loginId);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        // 验证讨论是否存在
        Discussion discussion = discussionService.getDiscussion(discussionId);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        // 验证讨论是否已关闭
        if (discussion.getIsClosed()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "该讨论已关闭，无法回复");
            return ResponseEntity.status(400).body(response);
        }
        // 设置讨论ID
        reply.setDiscussionId(discussionId);
        // 如果是教师回复，设置标记
        reply.setIsTeacherReply(user.getRole() == User.UserRole.teacher);
        // 兼容从 body 中传入 parentReplyId 的情况
        if (parentReplyId == null && reply.getParentReplyId() != null) {
            parentReplyId = reply.getParentReplyId();
        }
        // 验证父回复是否存在（如果是回复其他回复）
        if (parentReplyId != null) {
            DiscussionReply parentReply = discussionReplyService.getReply(parentReplyId);
            if (parentReply == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "父回复不存在");
                return ResponseEntity.status(400).body(response);
            }
            // 确保父回复属于同一个讨论
            if (!parentReply.getDiscussionId().equals(discussionId)) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "父回复不属于该讨论");
                return ResponseEntity.status(400).body(response);
            }
            reply.setParentReplyId(parentReplyId);
        }
        reply.setUserId(loginId);
        reply.setUserNum(user.getUserId());  // 设置用户编号
        try {
            return ResponseEntity.ok(discussionReplyService.createReply(reply));
        } catch (IllegalArgumentException e) {
            Map<String, String> response = new HashMap<>();
            String errorMessage = e.getMessage();
            if ("Parent reply does not exist".equals(errorMessage)) {
                response.put("error", "父回复不存在");
            } else if ("Parent reply does not belong to this discussion".equals(errorMessage)) {
                response.put("error", "父回复不属于该讨论");
            } else {
                response.put("error", "创建回复失败: " + errorMessage);
            }
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            logger.error("创建回复失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "创建回复失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * 更新回复
     * 权限要求：已登录用户
     * 功能说明：只能更新自己创建的回复
     * 返回：403 - 无权限修改；404 - 回复不存在
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReply(
            @PathVariable Long id,
            @RequestBody DiscussionReply reply) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 获取当前登录用户信息
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(loginId);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }

        // 验证是否是回复创建者
        DiscussionReply existingReply = discussionReplyService.getReply(id);
        if (existingReply == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "回复不存在");
            return ResponseEntity.status(404).body(response);
        }

        // 验证权限
        if (!existingReply.getUserId().equals(loginId)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "您没有权限修改此回复");
            return ResponseEntity.status(403).body(response);
        }

        // 保持原有字段的值
        reply.setId(id);
        reply.setUserId(existingReply.getUserId());
        reply.setUserNum(user.getUserId());  // 设置用户编号
        reply.setDiscussionId(existingReply.getDiscussionId());
        reply.setParentReplyId(existingReply.getParentReplyId());
        reply.setIsTeacherReply(existingReply.getIsTeacherReply());

        try {
            return ResponseEntity.ok(discussionReplyService.updateReply(reply));
        } catch (Exception e) {
            logger.error("更新回复失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "更新回复失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * 删除回复
     * 权限要求：已登录用户
     * 功能说明：只能删除自己创建的回复
     * 返回：403 - 无权限删除；404 - 回复不存在
     */
    @DeleteMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<?> deleteReply(@PathVariable Long id) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 验证是否是回复创建者
        DiscussionReply reply = discussionReplyService.getReply(id);
        if (reply == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "回复不存在");
            return ResponseEntity.status(404).body(response);
        }
        if (!reply.getUserId().equals(StpUtil.getLoginIdAsLong())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "您没有权限删除此回复");
            return ResponseEntity.status(403).body(response);
        }

        try {
            discussionReplyService.deleteReply(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "回复删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "删除回复失败");
            return ResponseEntity.status(400).body(response);
        }
    }

    /**
     * 获取回复详情
     * 权限要求：已登录用户
     * 功能说明：获取指定ID的回复详情
     * 返回：404 - 回复不存在
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<DiscussionReply> getReply(@PathVariable Long id) {
        DiscussionReply reply = discussionReplyService.getReply(id);
        if (reply != null) {
            return ResponseEntity.ok(reply);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取讨论下的所有回复
     * 权限要求：已登录用户
     * 功能说明：获取指定讨论ID下的所有回复列表
     */
    @GetMapping("/discussion/{discussionId}")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionReply>> getRepliesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionReplyService.getRepliesByDiscussion(discussionId));
    }

    /**
     * 获取用户的所有回复
     * 权限要求：已登录用户
     * 功能说明：获取指定用户ID的所有回复列表
     */
    @GetMapping("/user/{userId}")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionReply>> getRepliesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(discussionReplyService.getRepliesByUser(userId));
    }

    /**
     * 获取讨论的顶层回复
     * 权限要求：已登录用户
     * 功能说明：获取指定讨论ID下的所有顶层回复（非回复其他回复的回复）
     */
    @GetMapping("/discussion/{discussionId}/top-level")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionReply>> getTopLevelReplies(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionReplyService.getTopLevelReplies(discussionId));
    }

    /**
     * 获取指定回复的子回复
     * 权限要求：已登录用户
     * 功能说明：获取指定父回复ID下的所有子回复列表
     */
    @GetMapping("/parent/{parentReplyId}")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionReply>> getRepliesByParent(@PathVariable Long parentReplyId) {
        return ResponseEntity.ok(discussionReplyService.getRepliesByParent(parentReplyId));
    }

    /**
     * 获取讨论的教师回复
     * 权限要求：已登录用户
     * 功能说明：获取指定讨论ID下的所有教师回复列表
     */
    @GetMapping("/discussion/{discussionId}/teacher")
    @SaCheckLogin
    public ResponseEntity<List<DiscussionReply>> getTeacherReplies(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionReplyService.getTeacherReplies(discussionId));
    }

    /**
     * 获取讨论的回复数量
     * 权限要求：已登录用户
     * 功能说明：统计指定讨论ID下的回复总数
     */
    @GetMapping("/discussion/{discussionId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countRepliesByDiscussion(@PathVariable Long discussionId) {
        return ResponseEntity.ok(discussionReplyService.countRepliesByDiscussion(discussionId));
    }

    /**
     * 获取用户的回复数量
     * 权限要求：已登录用户
     * 功能说明：统计指定用户ID的回复总数
     */
    @GetMapping("/user/{userId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countRepliesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(discussionReplyService.countRepliesByUser(userId));
    }

    /**
     * 删除讨论的所有回复
     * 权限要求：教师角色
     * 功能说明：删除指定讨论ID下的所有回复
     */
    @DeleteMapping("/discussion/{discussionId}")
    public ResponseEntity<?> deleteRepliesByDiscussion(@PathVariable Long discussionId) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 获取当前登录用户信息
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(loginId);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }

        // 验证用户角色
        if (user.getRole() != User.UserRole.teacher) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "只有教师才能执行此操作");
            return ResponseEntity.status(403).body(response);
        }

        // 验证讨论是否存在
        Discussion discussion = discussionService.getDiscussion(discussionId);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }

        try {
            discussionReplyService.deleteRepliesByDiscussion(discussionId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "讨论的所有回复已删除");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("删除讨论回复失败", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "删除回复失败: " + e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }
}