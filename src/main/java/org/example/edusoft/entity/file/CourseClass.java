package org.example.edusoft.entity.file;

import java.util.Date;

/**
 * 课程-班级关系实体类
 */
public class CourseClass {
    private Long courseId;
    private Long classId;
    private Date joinedAt;

    // Getter and Setter
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Date getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Date joinedAt) { this.joinedAt = joinedAt; }
}
