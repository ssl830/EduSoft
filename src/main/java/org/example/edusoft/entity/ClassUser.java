// src/main/java/org/example/edusoft/entity/user/User.java
package org.example.edusoft.entity;

import java.util.Date;

/**
 * @Date: 2025/5/15 11:30
 * @Description: 班级-用户关系实体类
 */
public class ClassUser {
    private Long classId;
    private Long userId;
    private Date joinedAt;

    // Getter and Setter
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }
}