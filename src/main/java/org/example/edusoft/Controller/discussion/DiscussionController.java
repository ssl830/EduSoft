package org.example.edusoft.controller.discussion;

import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.service.discussion.DiscussionService;
import org.example.edusoft.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.example.edusoft.entity.user.User;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;
    
    @Autowired
    private UserService userService;

    /**
     * 创建讨论
     * 权限要求：已登录用户
     * 功能说明：创建新的讨论，自动设置创建者ID为当前登录用户
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param discussion 讨论内容
     */
    @PostMapping("/course/{courseId}/class/{classId}")
    @SaCheckLogin
    public ResponseEntity<?> createDiscussion(
            @PathVariable(required = true) Long courseId,
            @PathVariable(required = true) Long classId,
            @RequestBody Discussion discussion) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }

        // 验证课程ID和班级ID
        if (courseId <= 0) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "无效的课程ID");
            return ResponseEntity.badRequest().body(response);
        }

        if (classId <= 0) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "无效的班级ID");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 获取当前登录用户信息
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userService.findById(loginId);
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        discussion.setCourseId(courseId);
        discussion.setClassId(classId);
        discussion.setCreatorId(loginId);
        discussion.setCreatorNum(user.getUserId());
        return ResponseEntity.ok(discussionService.createDiscussion(discussion));
    }

    /**
     * 更新讨论
     * 权限要求：已登录用户
     * 功能说明：只能更新自己创建的讨论
     * 返回：403 - 无权限修改；404 - 讨论不存在
     */
    @PutMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<?> updateDiscussion(
            @PathVariable Long id,
            @RequestBody Discussion discussion) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        // 验证是否是讨论创建者
        Discussion existingDiscussion = discussionService.getDiscussion(id);
        if (existingDiscussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        if (!existingDiscussion.getCreatorId().equals(StpUtil.getLoginIdAsLong())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "您没有权限修改此讨论");
            return ResponseEntity.status(403).body(response);
        }
        
        // 保持原有字段的值
        discussion.setId(id);
        discussion.setCreatorId(existingDiscussion.getCreatorId());
        discussion.setClassId(existingDiscussion.getClassId());
        discussion.setCourseId(existingDiscussion.getCourseId());
        
        return ResponseEntity.ok(discussionService.updateDiscussion(discussion));
    }

    /**
     * 删除讨论
     * 权限要求：已登录用户
     * 功能说明：只能删除自己创建的讨论
     * 返回：403 - 无权限删除；404 - 讨论不存在
     */
    @DeleteMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<?> deleteDiscussion(@PathVariable Long id) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        // 验证是否是讨论创建者
        Discussion discussion = discussionService.getDiscussion(id);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        if (!discussion.getCreatorId().equals(StpUtil.getLoginIdAsLong())) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "您没有权限删除此讨论");
            return ResponseEntity.status(403).body(response);
        }
        discussionService.deleteDiscussion(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "讨论删除成功");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取讨论详情
     * 权限要求：已登录用户
     * 功能说明：获取讨论详情并增加浏览次数
     * 返回：404 - 讨论不存在
     */
    @GetMapping("/{id}")
    @SaCheckLogin
    public ResponseEntity<?> getDiscussion(@PathVariable Long id) {
        if (!StpUtil.isLogin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        Discussion discussion = discussionService.getDiscussion(id);
        if (discussion != null) {
            discussionService.incrementViewCount(id);
            return ResponseEntity.ok(discussion);
        }
        Map<String, String> response = new HashMap<>();
        response.put("error", "讨论不存在");
        return ResponseEntity.status(404).body(response);
    }

    /**
     * 获取课程下的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定课程ID下的所有讨论列表
     */
    @GetMapping("/course/{courseId}")
    @SaCheckLogin
    public ResponseEntity<List<Discussion>> getDiscussionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByCourse(courseId));
    }

    /**
     * 获取班级下的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定班级ID下的所有讨论列表
     */
    @GetMapping("/class/{classId}")
    @SaCheckLogin
    public ResponseEntity<List<Discussion>> getDiscussionsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByClass(classId));
    }

    /**
     * 获取用户创建的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定用户ID创建的所有讨论列表
     */
    @GetMapping("/creator/{creatorId}")
    @SaCheckLogin
    public ResponseEntity<List<Discussion>> getDiscussionsByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByCreator(creatorId));
    }

    /**
     * 获取课程和班级下的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定课程ID和班级ID下的所有讨论列表
     */
    @GetMapping("/course/{courseId}/class/{classId}")
    @SaCheckLogin
    public ResponseEntity<List<Discussion>> getDiscussionsByCourseAndClass(
            @PathVariable Long courseId,
            @PathVariable Long classId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByCourseAndClass(courseId, classId));
    }

    /**
     * 更新讨论置顶状态
     * 权限要求：教师角色
     * 功能说明：设置讨论是否置顶
     */
    @PutMapping("/{id}/pin")
    public ResponseEntity<?> updatePinnedStatus(
            @PathVariable Long id,
            @RequestParam Boolean isPinned) {
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
        
        Discussion discussion = discussionService.getDiscussion(id);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        discussionService.updatePinnedStatus(id, isPinned);
        Map<String, String> response = new HashMap<>();
        response.put("message", isPinned ? "讨论已置顶" : "讨论已取消置顶");
        return ResponseEntity.ok(response);
    }

    /**
     * 更新讨论关闭状态
     * 权限要求：教师角色
     * 功能说明：设置讨论是否关闭
     */
    @PutMapping("/{id}/close")
    public ResponseEntity<?> updateClosedStatus(
            @PathVariable Long id,
            @RequestParam Boolean isClosed) {
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
        
        Discussion discussion = discussionService.getDiscussion(id);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        discussionService.updateClosedStatus(id, isClosed);
        Map<String, String> response = new HashMap<>();
        response.put("message", isClosed ? "讨论已关闭" : "讨论已重新开放");
        return ResponseEntity.ok(response);
    }

    /**
     * 获取课程下的讨论数量
     * 权限要求：已登录用户
     * 功能说明：统计指定课程ID下的讨论总数
     */
    @GetMapping("/course/{courseId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countDiscussionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(discussionService.countDiscussionsByCourse(courseId));
    }

    /**
     * 获取班级下的讨论数量
     * 权限要求：已登录用户
     * 功能说明：统计指定班级ID下的讨论总数
     */
    @GetMapping("/class/{classId}/count")
    @SaCheckLogin
    public ResponseEntity<Integer> countDiscussionsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(discussionService.countDiscussionsByClass(classId));
    }
}