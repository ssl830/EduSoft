package org.example.edusoft.content.controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.dto.file.FileQueryRequest;
import org.example.edusoft.content.dto.file.FileResponseDTO;
import org.example.edusoft.content.entity.file.FileAccessDTO;
import org.example.edusoft.content.exception.BusinessException;
import org.example.edusoft.content.service.file.FileDownloadService;
import org.example.edusoft.content.service.file.FileUpload;
import org.example.edusoft.content.service.file.FilePreviewService;
import org.example.edusoft.content.service.file.FileQueryService;
import org.example.edusoft.content.service.file.FolderService;
import org.example.edusoft.content.service.file.FileAccessService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.example.edusoft.content.client.UserClient;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileDownloadService fileDownloadService;
    private final FileUpload fileUploadService;
    private final FileQueryService fileQueryService;
    private final FolderService folderService;
    private final FilePreviewService filePreviewService;
    private final FileAccessService fileAccessService;

    public FileController(FileDownloadService fileDownloadService, FileUpload fileUploadService, FileQueryService fileQueryService, FolderService folderService, FilePreviewService filePreviewService, FileAccessService fileAccessService) {
        this.fileDownloadService = fileDownloadService;
        this.fileUploadService = fileUploadService;
        this.fileQueryService = fileQueryService;
        this.folderService = folderService;
        this.filePreviewService = filePreviewService;
        this.fileAccessService = fileAccessService;
    }

    @Autowired
    private UserClient userClient;

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

   /**
     * 获取用户在某个课程下的文件列表（支持过滤）, done
     * done
     * @param request 包含过滤条件的请求体 
     * @return Result<List<FileResponseDTO>>
     */
    @PostMapping("/resources/{courseId}/filelist")
    public Result<List<FileResponseDTO>> getFilesByUserAndCourse(@PathVariable("courseId") Long courseId,
    @RequestBody FileQueryRequest request) {
         // 参数校验
        if (request.getUserId() == null || request.getCourseId() == null) {
            return Result.error("用户ID和课程ID不能为空");
        }

        // 构造查询参数
        Long chapter = request.getChapter();
        String type = request.getType();  // PDF, PPT, VIDEO, CODE, OTHER
        String title = request.getTitle(); // 文件名模糊匹配
        Long userId = request.getUserId();
        Boolean isTeacher = request.getIsTeacher();
        //Long courseId = request.getCourseId();

        // 调用 Service 查询文件列表
        List<FileResponseDTO> files = fileQueryService.getFilesByUserandCourseWithFilter(
            userId,
            courseId,
            title,
            type,
            chapter,
            isTeacher
        );
        System.out.println("FileController: getFilesByUserAndCourse");
        System.out.println(files);
        return Result.success(files, "获取用户课程文件成功");
    }

    /**
     * 文件上传接口 done
     *
     * @param courseId   所属课程ID（路径参数）
     * @param file       上传的文件（表单参数）
     * @param title      文件标题（表单参数）
     * @param sectionId  所属章节ID（可选）
     * @param uploaderId 上传者ID（可选）
     * @param visibility 可见性（PUBLIC / COURSE_ONLY）
     * @param type       文件类型（可选）
     * @return Result<?> 响应结果
     */
    @PostMapping("resources/{courseId}/upload")
    public Result<Object> uploadFile(
        @PathVariable("courseId") Long courseId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("title") String title,
        @RequestParam(name = "sectionId", required = false) Long sectionId,
        @RequestParam(name = "uploaderId", required = false) Long uploaderId,
        @RequestParam(name = "visibility", required = false, defaultValue = "CLASS_ONLY") String visibility,
        @RequestParam(name = "type", required = false) String type,
        @RequestParam(name = "uploadToKnowledgeBase", required = false, defaultValue = "false") Boolean uploadToKnowledgeBase
    ) throws JsonProcessingException {
        // 参数校验
        if (file == null || file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        if (title == null || title.trim().isEmpty()) {
            return Result.error("文件标题不能为空");
        }

        if (!"PUBLIC".equals(visibility) && !"CLASS_ONLY".equals(visibility)) {
            return Result.error("visibility 参数非法");
        }

        // 调用 FileUploadService 进行文件上传逻辑处理
        return (Result<Object>) fileUploadService.uploadFile(file, title, courseId, sectionId, visibility, uploaderId, type, uploadToKnowledgeBase);
    }


    /**
     * 文件下载接口
     *
     * @param resourceId 文件或文件夹ID
     * @return Result<FileAccessDTO> 文件下载URL及相关信息
     */
    @GetMapping("/resources/{resourceId}/download")
    public Result<FileAccessDTO> downloadResource(@PathVariable("resourceId") String resourceId) {
        try {
            Long fileId = Long.valueOf(resourceId);
            return Result.success(fileAccessService.getDownloadUrl(fileId), "获取下载链接成功");
        } catch (NumberFormatException e) {
            return Result.error("无效的 resourceId 格式");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }


    /**
     * 文件预览接口
     *
     * @param resourceId 文件ID
     * @return Result<FileAccessDTO> 文件预览URL及相关信息
     */
    @GetMapping("/resources/{resourceId}/preview")
    public Result<FileAccessDTO> previewResource(@PathVariable("resourceId") String resourceId) {
        try {
            Long fileId = Long.valueOf(resourceId);
            return Result.success(fileAccessService.getPreviewUrl(fileId), "获取预览链接成功");
        } catch (NumberFormatException e) {
            return Result.error("无效的 resourceId 格式");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取文件预览URL
     *
     * @param resourceId 文件ID
     * @return 文件预览URL及相关信息
     */
    @GetMapping("/resources/{resourceId}/preview-url")
    public Result<FileAccessDTO> getPreviewUrl(@PathVariable("resourceId") String resourceId) {
        try {
            Long fileId = Long.valueOf(resourceId);
            FileAccessDTO accessDTO = fileAccessService.getPreviewUrl(fileId);
            return Result.success(accessDTO, "获取预览链接成功");
        } catch (NumberFormatException e) {
            return Result.error("无效的 resourceId 格式");
        } catch (BusinessException e) {
            return Result.error(e.getMessage());
        }
    }
}