package org.example.edusoft.entity.course;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("courseclass")
public class CourseClass {
    private Long courseId;
    private Long classId;
    private LocalDateTime joinedAt;
} 