package org.example.edusoft.entity.file;

import lombok.Data;
import lombok.Builder;

/**
 * 文件访问DTO
 */
@Data
@Builder
public class FileAccessDTO {
    /**
     * 带签名的访问URL
     */
    private String url;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * URL过期时间（秒）
     */
    private Integer expiresIn;
} 