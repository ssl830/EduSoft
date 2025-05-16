package org.example.edusoft.entity.file;

import javax.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件信息实体类
 *
 * @Author: sjy
 * @Date: 2025/5/15 11:30
 */
@Data
public class FileInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Boolean isDir;         // 是否为文件夹
    private Long parentId;         // 父节点ID
    private Long courseId;
    private Long classId;
    private Long uploaderId;
    private String visibility;
    private String path;           // 可选：物化路径
    private Date createdAt;
    private Date updatedAt;
    /* 
    private Long id;            // 自增ID
    private String fileType;    // 文件类型（AUDIO_VIDEO, IMAGE, TEXT, PDF, OTHER）
    private Long fileSize;      // 文件大小（字节）
    private String fileName;    // 文件名称
    private Long courseId;      // 所属课程 ID
    private Long sectionId;     // 章节 ID
    private Long classId;       // 可见班级 ID
    private Integer fileVersion;    // 版本号，默认为 1
    private Long uploaderId;    // 上传者用户 ID
    private String visibility;  // 可见性（PUBLIC, PRIVATE, CLASS_ONLY）
    private String fileLocation;    // 在课程中的位置（COURSE_FILE, SECTION_FILE, PRACTICE_FILE, OTHER）
    private Date uploadTime;    // 上传时间
 */
    // 以下字段不在数据库中，仅用于前端展示或业务逻辑
    @Transient
    private String url;         // 文件路径（非数据库字段）
    @Transient
    private String suffix;      // 文件后缀名（非数据库字段）
    @Transient
    private Boolean isImg;      // 是否图片（非数据库字段）
    @Transient
    private String source;      // 来源（非数据库字段）
    @Transient
    private String rename;      // 重命名值（非数据库字段）
    @Transient
    private String dirIds;      // 目录ID拼接（非数据库字段）
}
