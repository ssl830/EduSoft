package org.example.edusoft.entity.file;

import jakarta.persistence.*;
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
    private long sectiondirId;      // 班级文件夹中的章节文件夹ID，其与其内部文件的sectionId相同
    private FileType fileType;      // 文件类型
    private Long sectionId;        
    private Long lastVersionId;  // 指向该文件的上一个版本的id，如果没有上一个版本默认为0     
    private Boolean isCurrentVersion;   // 是否为当前版本，没有多个版本时默认为true   
    private Long fileSize;                // 文件大小
    private String visibility;   
    private Date createdAt;
    private Date updatedAt;
    private String url;          
    private int version;    // 显示给前端的版本号？？
    private String objectName;     // 对象存储中的文件名 
   
    // 以下字段不在数据库中，仅用于前端展示或业务逻辑
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

    public FileType getFileType() { return fileType; }
    public void setFileType(FileType fileType) { this.fileType = fileType; }
}
