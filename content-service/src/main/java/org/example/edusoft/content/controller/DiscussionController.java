package org.example.edusoft.content.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.edusoft.content.entity.discussion.Discussion;
import org.example.edusoft.content.service.discussion.DiscussionService;
import org.example.edusoft.content.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/discussion")
public class DiscussionController {

    @Autowired
    private DiscussionService discussionService;
    
    @Autowired
    private UserClient userClient;

    /**
     * 创建讨论
     * 权限要求：已登录用户
     * 功能说明：创建新的讨论，自动设置创建者ID为当前登录用户
     * @param courseId 课程ID
     * @param classId 班级ID
     * @param discussion 讨论内容
     */
    @PostMapping("/course/{courseId}/class/{classId}")
    public ResponseEntity<?> createDiscussion(
            @PathVariable(required = true) Long courseId,
            @PathVariable(required = true) Long classId,
            @RequestBody Discussion discussion,
            HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
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
        
        // 调用 user-service 校验 token 并获取用户信息
        boolean validate = userClient.validateToken("http://localhost:8081", token);
        if (validate == false) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录状态无效");
            return ResponseEntity.status(401).body(response);
        }
        Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
        if (userData == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        Long loginId = ((Number) userData.get("id")).longValue();
        String userNum = (String) userData.get("userId");
        
        discussion.setCourseId(courseId);
        discussion.setClassId(classId);
        discussion.setCreatorId(loginId);
        if (userNum != null) {
            discussion.setCreatorNum(userNum);
        }
        return ResponseEntity.ok(discussionService.createDiscussion(discussion));
    }

    /**
     * 更新讨论
     * 权限要求：已登录用户
     * 功能说明：只能更新自己创建的讨论
     * 返回：403 - 无权限修改；404 - 讨论不存在
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDiscussion(
            @PathVariable Long id,
            @RequestBody Discussion discussion,
            HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        boolean validate = userClient.validateToken("http://localhost:8081", token);
        if (validate == false) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录状态无效");
            return ResponseEntity.status(401).body(response);
        }
        Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
        Long loginId = ((Number) userData.get("id")).longValue();
        // 验证是否是讨论创建者
        Discussion existingDiscussion = discussionService.getDiscussion(id);
        if (existingDiscussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        if (!existingDiscussion.getCreatorId().equals(loginId)) {
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
    public ResponseEntity<?> deleteDiscussion(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        boolean validate = userClient.validateToken("http://localhost:8081", token);
        if (validate == false) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录状态无效");
            return ResponseEntity.status(401).body(response);
        }
        Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
        Long loginId = ((Number) userData.get("id")).longValue();
        // 验证是否是讨论创建者
        Discussion discussion = discussionService.getDiscussion(id);
        if (discussion == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "讨论不存在");
            return ResponseEntity.status(404).body(response);
        }
        if (!discussion.getCreatorId().equals(loginId)) {
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
    public ResponseEntity<?> getDiscussion(@PathVariable Long id, HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
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
    public ResponseEntity<List<Discussion>> getDiscussionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByCourse(courseId));
    }

    /**
     * 获取班级下的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定班级ID下的所有讨论列表
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<List<Discussion>> getDiscussionsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByClass(classId));
    }

    /**
     * 获取用户创建的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定用户ID创建的所有讨论列表
     */
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<List<Discussion>> getDiscussionsByCreator(@PathVariable Long creatorId) {
        return ResponseEntity.ok(discussionService.getDiscussionsByCreator(creatorId));
    }

    /**
     * 获取课程和班级下的所有讨论
     * 权限要求：已登录用户
     * 功能说明：获取指定课程ID和班级ID下的所有讨论列表
     */
    @GetMapping("/course/{courseId}/class/{classId}")
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
            @RequestParam Boolean isPinned,
            HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        boolean validate = userClient.validateToken("http://localhost:8081", token);
        if (validate == false) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录状态无效");
            return ResponseEntity.status(401).body(response);
        }
        Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
        if (userData == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        // 验证用户角色（仅教师可操作）
        String role = (String) userData.get("role");
        if (role == null || !"teacher".equals(role)) {
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
            @RequestParam Boolean isClosed,
            HttpServletRequest request) {
        String token = request.getHeader("satoken");
        if (token == null || token.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "请先登录");
            return ResponseEntity.status(401).body(response);
        }
        boolean validate = userClient.validateToken("http://localhost:8081", token);
        if (validate == false) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "登录状态无效");
            return ResponseEntity.status(401).body(response);
        }
        Map<String, Object> userData = userClient.fetchCurrentUser("http://localhost:8081", token);
        if (userData == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "用户不存在");
            return ResponseEntity.status(404).body(response);
        }
        
        // 验证用户角色（仅教师可操作）
        String role = (String) userData.get("role");
        if (role == null || !"teacher".equals(role)) {
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
    public ResponseEntity<Integer> countDiscussionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(discussionService.countDiscussionsByCourse(courseId));
    }

    /**
     * 获取班级下的讨论数量
     * 权限要求：已登录用户
     * 功能说明：统计指定班级ID下的讨论总数
     */
    @GetMapping("/class/{classId}/count")
    public ResponseEntity<Integer> countDiscussionsByClass(@PathVariable Long classId) {
        return ResponseEntity.ok(discussionService.countDiscussionsByClass(classId));
    }
}