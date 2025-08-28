package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileAccessDTO;

/**
 * 文件访问服务接口
 */
public interface FileAccessService {
    /**
     * 获取文件下载URL
     * @param fileId 文件ID
     * @return 文件访问信息
     */
    FileAccessDTO getDownloadUrl(Long fileId);

    /**
     * 获取文件预览URL
     * @param fileId 文件ID
     * @return 文件访问信息
     */
    FileAccessDTO getPreviewUrl(Long fileId);

    /**
     * 通过对象名获取文件下载URL
     * @param objectName 对象存储中的文件名
     * @return 文件访问信息
     */
    FileAccessDTO getDownloadUrlByObjectName(String objectName);

    /**
     * 通过对象名获取文件预览URL
     * @param objectName 对象存储中的文件名
     * @return 文件访问信息
     */
    FileAccessDTO getPreviewUrlByObjectName(String objectName);
} 