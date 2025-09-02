package org.example.edusoft.learning.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("self_practice")
public class SelfPractice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long studentId;
    private String title;
    private LocalDateTime createdAt;
}
