package org.example.edusoft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@TableName("coursesection")
public class CourseSection {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    @NotNull(message = "章节标题不能为空")
    @Size(min = 1, max = 200, message = "章节标题长度必须在1-200个字符之间")
    private String title;
    
    private Integer sortOrder;
}
