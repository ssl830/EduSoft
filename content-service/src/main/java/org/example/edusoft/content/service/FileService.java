package org.example.edusoft.content.service;

import org.example.edusoft.content.entity.file.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    
    /**
     * 上传文件
     */
    FileInfo uploadFile(MultipartFile file, String description, String category, Long uploaderId, String uploaderName);
    
//    /**
//     * 根据ID获取文件信息
//     */
//    FileInfo getFileById(Long id);
//
//    /**
//     * 根据上传者ID获取文件列表
//     */
//    List<FileInfo> getFilesByUploaderId(Long uploaderId);
//
//    /**
//     * 根据分类获取文件列表
//     */
//    List<FileInfo> getFilesByCategory(String category);
//
//    /**
//     * 根据可见性获取文件列表
//     */
//    List<FileInfo> getFilesByVisibility(String visibility);
//
//    /**
//     * 获取所有活跃文件
//     */
//    List<FileInfo> getAllActiveFiles();
//
//    /**
//     * 删除文件
//     */
//    void deleteFile(Long id);
//
//    /**
//     * 更新文件描述
//     */
//    void updateFileDescription(Long id, String description);
//
//    /**
//     * 更新文件可见性
//     */
//    void updateFileVisibility(Long id, String visibility);
}
