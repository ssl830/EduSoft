package org.example.edusoft.controller.file;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.service.file.FileService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/courses")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/{courseId}/resources")
    public Result<?> uploadFile(
            @PathVariable Long courseId,
            @RequestParam String title,
            @RequestParam String type,
            @RequestParam Long sectionId,
            @RequestParam Long uploaderId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String visibility) {

        // 调用 Service 完成整个上传逻辑
        return fileService.uploadFile(courseId, sectionId, uploaderId, file, visibility);
    }
}

