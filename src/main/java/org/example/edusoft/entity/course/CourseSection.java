package org.example.edusoft.entity.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("CourseSection")
public class CourseSection {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String title;
    private Integer sortOrder;
} 