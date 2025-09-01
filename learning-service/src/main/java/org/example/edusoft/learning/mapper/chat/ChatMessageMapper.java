package org.example.edusoft.learning.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.learning.entity.chat.ChatMessage;

import java.util.List;

/**
 * 聊天消息Mapper
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 获取会话的消息列表，按消息顺序排序
     */
    @Select("SELECT id, session_id, role, content, `references`, knowledge_points, message_order, created_at, token_count " +
            "FROM chat_message WHERE session_id = #{sessionId} ORDER BY message_order ASC")
    List<ChatMessage> getSessionMessages(@Param("sessionId") Long sessionId);
    
    /**
     * 获取会话最近的N条消息
     */
    @Select("SELECT id, session_id, role, content, `references`, knowledge_points, message_order, created_at, token_count " +
            "FROM chat_message WHERE session_id = #{sessionId} ORDER BY message_order DESC LIMIT #{limit}")
    List<ChatMessage> getRecentMessages(@Param("sessionId") Long sessionId, @Param("limit") Integer limit);
    
    /**
     * 获取会话中指定范围的消息
     */
    @Select("SELECT id, session_id, role, content, `references`, knowledge_points, message_order, created_at, token_count " +
            "FROM chat_message WHERE session_id = #{sessionId} AND message_order BETWEEN #{startOrder} AND #{endOrder} " +
            "ORDER BY message_order ASC")
    List<ChatMessage> getMessagesByRange(@Param("sessionId") Long sessionId, 
                                       @Param("startOrder") Integer startOrder, 
                                       @Param("endOrder") Integer endOrder);
    
    /**
     * 获取会话消息总数
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE session_id = #{sessionId}")
    Integer getMessageCount(@Param("sessionId") Long sessionId);
    
    /**
     * 获取会话中最大的消息顺序号
     */
    @Select("SELECT IFNULL(MAX(message_order), 0) FROM chat_message WHERE session_id = #{sessionId}")
    Integer getMaxMessageOrder(@Param("sessionId") Long sessionId);
}
