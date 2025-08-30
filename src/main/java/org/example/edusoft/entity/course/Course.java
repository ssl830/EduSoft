package org.example.edusoft.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "教师ID不能为空")
    private Long teacherId;
    
    @NotBlank(message = "课程名称不能为空")
    @Size(min = 2, max = 100, message = "课程名称长度必须在2-100个字符之间")
    private String name;
    
    @NotBlank(message = "课程代码不能为空")
    @Size(min = 4, max = 20, message = "课程代码长度必须在4-20个字符之间")
    private String code;
    
    @Size(max = 1000, message = "课程大纲长度不能超过1000个字符")
    private String outline;
    
    @Size(max = 500, message = "教学目标长度不能超过500个字符")
    private String objective;
    
    @Size(max = 500, message = "考核方式长度不能超过500个字符")
    private String assessment;
    
    private LocalDateTime createdAt;
} 