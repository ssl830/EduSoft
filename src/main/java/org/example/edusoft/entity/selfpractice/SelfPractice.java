package org.example.edusoft.entity.selfpractice;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("SelfPractice")
public class SelfPractice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private LocalDateTime createdAt;
} 