package org.example.edusoft.controller.file;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.common.dto.file.FileQueryRequest;
import org.example.edusoft.common.dto.file.FileResponseDTO;
import org.example.edusoft.service.file.FileDownloadService;
import org.example.edusoft.service.file.FileUpload;
import org.example.edusoft.service.file.FileQueryService;
import org.example.edusoft.service.file.FolderService;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FileController {

    private final FileDownloadService fileDownloadService;
    private final FileUpload fileUploadService;
    private final FileQueryService fileQueryService;

    public FileController(FileDownloadService fileDownloadService, FileUpload fileUploadService, FileQueryService fileQueryService, FolderService folderService) {
        this.fileDownloadService = fileDownloadService;
        this.fileUploadService = fileUploadService;
        this.fileQueryService = fileQueryService;
    }

    @PostMapping("/userfolders")
    public Result<List<FileResponseDTO>> getUserRootFolders(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        if (userId == null) {
            return Result.error("用户ID不能为空");
        }
        List<FileResponseDTO> rootFolders = fileQueryService.getAllFilesByUserId(userId);
        return Result.ok(rootFolders, "获取用户文件成功");
    }

   /**
     * 获取用户在某个课程下的文件列表（支持过滤）
     *
     * @param request 包含过滤条件的请求体 
     * @return Result<List<FileResponseDTO>>
     */
    @PostMapping("/courses/{courseId}/filelist")
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
        //Long courseId = request.getCourseId();

        // 调用 Service 查询文件列表
        List<FileResponseDTO> files = fileQueryService.getFilesByUserandCourseWithFilter(
            userId,
            courseId,
            title,
            type,
            chapter
        );
        return Result.ok(files, "获取用户课程文件成功");
    }

    /**
     * 文件上传接口
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
    @PostMapping("courses/{courseId}/upload")
    public Result<?> uploadFile(
        @PathVariable("courseId") Long courseId,
        @RequestParam("file") MultipartFile file,
        @RequestParam("title") String title,
        @RequestParam(name = "sectionId", required = false) Long sectionId,
        @RequestParam(name = "uploaderId", required = false) Long uploaderId,
        @RequestParam(name = "visibility", required = false, defaultValue = "CLASS_ONLY") String visibility,
        @RequestParam(name = "type", required = false) String type
    ) {
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
        return fileUploadService.uploadFile(file, title, courseId, sectionId, visibility, uploaderId, type);
    }


    /**
     * 文件下载接口
     *
     * @param resourceId 文件或文件夹ID
     * @param response   HttpServletResponse 对象，用于写入响应流
     */
    @GetMapping("/resources/{resourceId}/download")
    public void downloadResource(@PathVariable("resourceId") String resourceId,
                                @RequestHeader(name = "Accept", required = false) String accept,
                                HttpServletResponse response) throws IOException {
        // resourceId 转换为 Long
        try {
            Long fileId = Long.valueOf(resourceId);
            fileDownloadService.downloadFileOrFolder(fileId, response);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的 resourceId 格式");
        }
    }
}

