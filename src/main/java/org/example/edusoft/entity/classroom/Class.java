package org.example.edusoft.entity.classroom;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Data
@TableName("Class")
public class Class {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "班级名称不能为空")
    @Size(min = 2, max = 50, message = "班级名称长度必须在2-50个字符之间")
    private String name;
    
    @NotNull(message = "班级代码不能为空")
    @Size(min = 6, max = 40, message = "班级代码长度必须在6-40个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "班级代码只能包含字母、数字和下划线")
    private String classCode;  // 班级暗号，用于学生加入
    
    @TableField(exist = false)  // 标记这个字段不是数据库字段
    private Long creatorId;  // 创建者ID字段，用于创建班级时建立关联关系
} 