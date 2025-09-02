package org.example.edusoft.content.controller;

import org.example.edusoft.content.dto.Result;
import org.example.edusoft.content.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/content/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 通用文件上传
     * @param file 要上传的文件
     * @param folder 存储文件夹路径
     * @return 文件访问URL
     */
    @PostMapping("/file")
    public Result<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "common") String folder) {
        try {
            if (file.isEmpty()) {
                return Result.error("上传文件不能为空");
            }
            
            // 检查文件大小 (500MB)
            long maxSize = 500 * 1024 * 1024L;
            if (file.getSize() > maxSize) {
                return Result.error("文件大小不能超过500MB");
            }
            
            String fileUrl = fileUploadService.uploadFile(file, folder);
            return Result.ok(fileUrl, "文件上传成功");
            
        } catch (Exception e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件
     * @param fileUrl 文件URL
     * @return 删除结果
     */
    @DeleteMapping("/file")
    public Result<Boolean> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        try {
            boolean success = fileUploadService.deleteFile(fileUrl);
            if (success) {
                return Result.ok(true, "文件删除成功");
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            return Result.error("文件删除失败：" + e.getMessage());
        }
    }

    /**
     * 生成文件签名访问URL
     * @param objectName 对象名称
     * @return 签名后的访问URL
     */
    @GetMapping("/signed-url")
    public Result<String> generateSignedUrl(@RequestParam("objectName") String objectName) {
        try {
            String signedUrl = fileUploadService.generateSignedUrl(objectName);
            return Result.ok(signedUrl, "生成签名URL成功");
        } catch (Exception e) {
            return Result.error("生成签名URL失败：" + e.getMessage());
        }
    }
}
