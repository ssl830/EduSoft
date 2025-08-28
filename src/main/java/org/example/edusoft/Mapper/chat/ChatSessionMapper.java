package org.example.edusoft.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.edusoft.entity.chat.ChatSession;

import java.util.List;

/**
 * 聊天会话Mapper
 */
@Mapper
public interface ChatSessionMapper extends BaseMapper<ChatSession> {
    
    /**
     * 获取用户的活跃会话列表，按更新时间倒序
     */
    List<ChatSession> getUserActiveSessions(@Param("userId") Long userId);
    
    /**
     * 设置用户的所有会话为非活跃状态
     */
    void deactivateAllUserSessions(@Param("userId") Long userId);
    
    /**
     * 更新会话的更新时间
     */
    void updateSessionTime(@Param("sessionId") Long sessionId);
    
    /**
     * 根据第一条消息内容更新会话标题
     */
    void updateSessionTitle(@Param("sessionId") Long sessionId, @Param("title") String title);
}
