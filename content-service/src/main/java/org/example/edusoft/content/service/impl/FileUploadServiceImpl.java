package org.example.edusoft.content.service.impl;

import org.example.edusoft.content.service.FileUploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    // 暂时使用模拟实现，避免OSS依赖问题
    
    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // 生成唯一的文件名
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String fileName = UUID.randomUUID().toString() + fileExtension;
            
            // 模拟文件上传，返回模拟URL
            return "https://mock-oss.example.com/" + folder + "/" + fileName;
            
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // 模拟文件删除
            return true;
        } catch (Exception e) {
            throw new RuntimeException("文件删除失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String generateSignedUrl(String objectName) {
        try {
            // 模拟生成签名URL
            return "https://mock-oss.example.com/signed/" + objectName + "?expires=" + System.currentTimeMillis();
        } catch (Exception e) {
            throw new RuntimeException("生成签名URL失败: " + e.getMessage(), e);
        }
    }
}
