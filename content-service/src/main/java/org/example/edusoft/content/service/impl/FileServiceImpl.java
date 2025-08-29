package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.entity.FileInfo;
import org.example.edusoft.content.mapper.FileMapper;
import org.example.edusoft.content.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public FileInfo uploadFile(MultipartFile file, String description, String category, Long uploaderId, String uploaderName) {
        try {
            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 确定文件类型
            String fileType = determineFileType(fileExtension);
            
            // 创建文件路径
            String uploadPath = "/uploads/" + category + "/";
            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            String filePath = uploadPath + fileName;
            Path fileLocation = Paths.get(filePath);
            Files.copy(file.getInputStream(), fileLocation);
            
            // 创建文件信息对象
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(fileName);
            fileInfo.setOriginalFileName(originalFilename);
            fileInfo.setFilePath(filePath);
            fileInfo.setFileType(fileType);
            fileInfo.setFileSize(file.getSize());
            fileInfo.setUploaderId(uploaderId);
            fileInfo.setUploaderName(uploaderName);
            fileInfo.setDescription(description);
            fileInfo.setCategory(category);
            fileInfo.setVisibility("CLASS_ONLY"); // 默认班级可见
            fileInfo.setObjectName(fileName);
            fileInfo.setFileUrl(filePath);
            fileInfo.setStatus("active");
            fileInfo.setCreatedAt(LocalDateTime.now());
            fileInfo.setUpdatedAt(LocalDateTime.now());
            
            // 保存到数据库
            fileMapper.insert(fileInfo);
            
            return fileInfo;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public FileInfo getFileById(Long id) {
        return fileMapper.findById(id);
    }

    @Override
    public List<FileInfo> getFilesByUploaderId(Long uploaderId) {
        return fileMapper.findByUploaderId(uploaderId);
    }

    @Override
    public List<FileInfo> getFilesByCategory(String category) {
        return fileMapper.findByCategory(category);
    }

    @Override
    public List<FileInfo> getFilesByVisibility(String visibility) {
        return fileMapper.findByVisibility(visibility);
    }

    @Override
    public List<FileInfo> getAllActiveFiles() {
        return fileMapper.findAllActive();
    }

    @Override
    public void deleteFile(Long id) {
        fileMapper.deleteById(id);
    }

    @Override
    public void updateFileDescription(Long id, String description) {
        fileMapper.updateDescription(id, description);
    }

    @Override
    public void updateFileVisibility(Long id, String visibility) {
        fileMapper.updateVisibility(id, visibility);
    }

    private String determineFileType(String fileExtension) {
        String extension = fileExtension.toLowerCase();
        if (extension.matches("\\.(mp4|avi|mov|wmv|flv|webm)$")) {
            return "VIDEO";
        } else if (extension.matches("\\.(ppt|pptx)$")) {
            return "PPT";
        } else if (extension.matches("\\.(java|py|c|cpp|js|html|css|php|sql)$")) {
            return "CODE";
        } else if (extension.matches("\\.(pdf)$")) {
            return "PDF";
        } else {
            return "OTHER";
        }
    }
}

