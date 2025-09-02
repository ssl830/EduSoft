package org.example.edusoft.content.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {
    
    /**
     * 上传文件到阿里云OSS
     * @param file 要上传的文件
     * @param folder 存储文件夹路径
     * @return 文件访问URL
     */
    String uploadFile(MultipartFile file, String folder);
    
    /**
     * 删除OSS中的文件
     * @param fileUrl 文件URL
     * @return 是否删除成功
     */
    boolean deleteFile(String fileUrl);
    
    /**
     * 生成文件访问URL（带签名，有效期1小时）
     * @param objectName 对象名称
     * @return 签名后的访问URL
     */
    String generateSignedUrl(String objectName);
}
