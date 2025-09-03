package org.example.edusoft.content.entity;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class StudyRecord {
    private Long id;
    private Long resourceId;
    private Long studentId;
    private Double progress;
    private Integer lastPosition;
    private Integer watchCount;
    private LocalDateTime lastWatchTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联字段
    private String resourceTitle;
    private String courseName;
    private String sectionTitle;

    // 用于Excel导出的格式化方法
    public String getFormattedProgress() {
        return String.format("%.2f%%", progress);
    }

    public String getFormattedLastWatchTime() {
        return lastWatchTime != null ?
                lastWatchTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) :
                "";
    }

    // 手动添加getter/setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Double getProgress() {
        return progress;
    }

    public void setProgress(Double progress) {
        this.progress = progress;
    }

    public Integer getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Integer lastPosition) {
        this.lastPosition = lastPosition;
    }

    public Integer getWatchCount() {
        return watchCount;
    }

    public void setWatchCount(Integer watchCount) {
        this.watchCount = watchCount;
    }

    public LocalDateTime getLastWatchTime() {
        return lastWatchTime;
    }

    public void setLastWatchTime(LocalDateTime lastWatchTime) {
        this.lastWatchTime = lastWatchTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }
}