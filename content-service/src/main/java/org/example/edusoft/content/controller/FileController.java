package org.example.edusoft.content.controller;

import org.example.edusoft.content.entity.FileInfo;
import org.example.edusoft.content.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/content/file")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<FileInfo> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "category", defaultValue = "other") String category,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("uploaderName") String uploaderName,
            @RequestParam(value = "visibility", defaultValue = "CLASS_ONLY") String visibility,
            @RequestParam(value = "objectName", required = false) String objectName,
            @RequestParam(value = "fileUrl", required = false) String fileUrl) {
        
        FileInfo fileInfo = fileService.uploadFile(file, description, category, uploaderId, uploaderName);
        return ResponseEntity.ok(fileInfo);
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            FileInfo fileInfo = fileService.getFileById(id);
            if (fileInfo == null) {
                return ResponseEntity.notFound().build();
            }
            
            Path filePath = Paths.get(fileInfo.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getOriginalFileName() + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable Long id) {
        FileInfo fileInfo = fileService.getFileById(id);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fileInfo);
    }

    /**
     * 根据上传者获取文件列表
     */
    @GetMapping("/uploader/{uploaderId}")
    public ResponseEntity<List<FileInfo>> getFilesByUploader(@PathVariable Long uploaderId) {
        List<FileInfo> files = fileService.getFilesByUploaderId(uploaderId);
        return ResponseEntity.ok(files);
    }

    /**
     * 根据分类获取文件列表
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<FileInfo>> getFilesByCategory(@PathVariable String category) {
        List<FileInfo> files = fileService.getFilesByCategory(category);
        return ResponseEntity.ok(files);
    }

    /**
     * 根据可见性获取文件列表
     */
    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<FileInfo>> getFilesByVisibility(@PathVariable String visibility) {
        List<FileInfo> files = fileService.getFilesByVisibility(visibility);
        return ResponseEntity.ok(files);
    }

    /**
     * 获取所有文件
     */
    @GetMapping("/all")
    public ResponseEntity<List<FileInfo>> getAllFiles() {
        List<FileInfo> files = fileService.getAllActiveFiles();
        return ResponseEntity.ok(files);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新文件描述
     */
    @PutMapping("/{id}/description")
    public ResponseEntity<Void> updateFileDescription(
            @PathVariable Long id,
            @RequestParam String description) {
        fileService.updateFileDescription(id, description);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新文件可见性
     */
    @PutMapping("/{id}/visibility")
    public ResponseEntity<Void> updateFileVisibility(
            @PathVariable Long id,
            @RequestParam String visibility) {
        fileService.updateFileVisibility(id, visibility);
        return ResponseEntity.ok().build();
    }
}
