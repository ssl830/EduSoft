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
    private Enum fileType;      // 文件类型
    private Long sectionId;        
    private int version;             
    private Long fileSize;                // 文件大小
    private String visibility;
    private String path;           // 可选：物化路径
    private Date createdAt;
    private Date updatedAt;
   
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
