package org.example.edusoft.learning.entity.ai;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_service_call_log")
public class AiServiceCallLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String endpoint;
    private Long durationMs;
    private LocalDateTime callTime;
    private String status;
    private String errorMessage;
}
