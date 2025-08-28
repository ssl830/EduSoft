package org.example.edusoft.mapper.chat;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.edusoft.entity.chat.ChatMemorySummary;

import java.util.List;

/**
 * 聊天记忆摘要Mapper
 */
@Mapper
public interface ChatMemorySummaryMapper extends BaseMapper<ChatMemorySummary> {
    
    /**
     * 获取会话的所有摘要
     */
    List<ChatMemorySummary> getSessionSummaries(@Param("sessionId") Long sessionId);
    
    /**
     * 获取会话的最新摘要
     */
    ChatMemorySummary getLatestSummary(@Param("sessionId") Long sessionId);
    
    /**
     * 计算会话摘要的总token数
     */
    Integer calculateSummaryTokenCount(@Param("sessionId") Long sessionId);
}
