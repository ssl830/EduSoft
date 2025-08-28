package org.example.edusoft.entity.classroom;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ClassUser")
public class ClassUser {
    private Long classId;
    private Long userId;
    private LocalDateTime joinedAt;
    
    @TableField(exist = false)
    private String studentName;  // 学生姓名
    
    @TableField(exist = false)
    private String studentId;    // 学生学号
} 