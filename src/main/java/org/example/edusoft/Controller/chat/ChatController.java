package org.example.edusoft.controller.chat;

import cn.dev33.satoken.annotation.SaCheckLogin;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.service.chat.ChatManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 聊天管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@SaCheckLogin
public class ChatController {
    
    @Autowired
    private ChatManagementService chatManagementService;
    
    /**
     * 获取用户的聊天会话列表
     */
    @GetMapping("/sessions")
    public Map<String, Object> getChatSessions() {
        try {
            log.info("Getting chat sessions for user");
            List<Map<String, Object>> sessions = chatManagementService.getUserChatSessions();
            log.info("Found {} chat sessions", sessions.size());
            return Map.of(
                "code", 200,
                "message", "success",
                "data", sessions
            );
        } catch (Exception e) {
            log.error("Error getting chat sessions: ", e);
            return Map.of(
                "code", 500,
                "message", "获取聊天会话失败：" + e.getMessage()
            );
        }
    }
    
    /**
     * 创建新的聊天会话
     */
    @PostMapping("/sessions")
    public Map<String, Object> createChatSession(@RequestBody Map<String, Object> request) {
        try {
            log.info("Creating new chat session with request: {}", request);
            String courseName = (String) request.get("courseName");
            Long courseId = request.get("courseId") instanceof Number ? 
                ((Number) request.get("courseId")).longValue() : null;
            
            Long sessionId = chatManagementService.createNewChatSession(courseName, courseId);
            log.info("Created chat session with ID: {}", sessionId);
            return Map.of(
                "code", 200,
                "message", "success",
                "data", Map.of("sessionId", sessionId)
            );
        } catch (Exception e) {
            log.error("Error creating chat session: ", e);
            return Map.of(
                "code", 500,
                "message", "创建聊天会话失败：" + e.getMessage()
            );
        }
    }
    
    /**
     * 获取会话详情和消息历史
     */
    @GetMapping("/sessions/{sessionId}")
    public Map<String, Object> getSessionDetail(@PathVariable Long sessionId) {
        try {
            Map<String, Object> detail = chatManagementService.getSessionDetail(sessionId);
            return Map.of(
                "code", 200,
                "message", "success",
                "data", detail
            );
        } catch (Exception e) {
            log.error("Error getting session detail: ", e);
            return Map.of(
                "code", 500,
                "message", "获取会话详情失败：" + e.getMessage()
            );
        }
    }
    
    /**
     * 删除聊天会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public Map<String, Object> deleteChatSession(@PathVariable Long sessionId) {
        try {
            chatManagementService.deleteChatSession(sessionId);
            return Map.of(
                "code", 200,
                "message", "删除成功"
            );
        } catch (Exception e) {
            log.error("Error deleting chat session: ", e);
            return Map.of(
                "code", 500,
                "message", "删除会话失败：" + e.getMessage()
            );
        }
    }
}
