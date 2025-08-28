package org.example.edusoft.controller.file;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.common.dto.file.FileQueryRequest;
import org.example.edusoft.common.dto.file.FileResponseDTO;
import org.example.edusoft.entity.file.FileAccessDTO;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.service.file.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileDownloadService fileDownloadService;
    @MockBean
    private FileUpload fileUploadService;
    @MockBean
    private FileQueryService fileQueryService;
    @MockBean
    private FolderService folderService;
    @MockBean
    private FilePreviewService filePreviewService;
    @MockBean
    private FileAccessService fileAccessService;

    // getUserRootFolders
    @Test
    void getUserRootFolders_success() throws Exception {
        List<FileResponseDTO> resp = Arrays.asList(new FileResponseDTO(), new FileResponseDTO());
        Mockito.when(fileQueryService.getAllFilesByUserId(1L)).thenReturn(resp);

        mockMvc.perform(post("/api/userfolders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取用户文件成功"));
    }

    @Test
    void getUserRootFolders_fail_nullUserId() throws Exception {
        mockMvc.perform(post("/api/userfolders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户ID不能为空"));
    }

    // getFilesByUserAndCourse
    @Test
    void getFilesByUserAndCourse_success() throws Exception {
        List<FileResponseDTO> resp = Arrays.asList(new FileResponseDTO());
        Mockito.when(fileQueryService.getFilesByUserandCourseWithFilter(any(), any(), any(), any(), any(), any())).thenReturn(resp);

        String json = "{\"userId\":1,\"courseId\":2,\"chapter\":1,\"type\":\"PDF\",\"title\":\"file\",\"isTeacher\":false}";
        mockMvc.perform(post("/api/courses/2/filelist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取用户课程文件成功"));
    }

    @Test
    void getFilesByUserAndCourse_fail_missingParams() throws Exception {
        String json = "{\"chapter\":1,\"type\":\"PDF\"}";
        mockMvc.perform(post("/api/courses/2/filelist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("用户ID和课程ID不能为空"));
    }

    // uploadFile
    @Test
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "file-content".getBytes());
        Mockito.when(fileUploadService.uploadFile(any(), anyString(), anyLong(), any(), anyString(), any(), any(), anyBoolean()))
                .thenReturn(Result.ok(null, "上传成功"));

        mockMvc.perform(multipart("/api/courses/2/upload")
                        .file(file)
                        .param("title", "test")
                        .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("上传成功"));
    }

    @Test
    void uploadFile_fail_noFile() throws Exception {
        mockMvc.perform(multipart("/api/courses/2/upload")
                        .param("title", "test")
                        .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("上传文件不能为空"));
    }

    @Test
    void uploadFile_fail_noTitle() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "file-content".getBytes());
        mockMvc.perform(multipart("/api/courses/2/upload")
                        .file(file)
                        .param("title", "  ")
                        .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("文件标题不能为空"));
    }

    @Test
    void uploadFile_fail_badVisibility() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "file-content".getBytes());
        mockMvc.perform(multipart("/api/courses/2/upload")
                        .file(file)
                        .param("title", "test")
                        .param("visibility", "XXX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("visibility 参数非法"));
    }

    // downloadResource
    @Test
    void downloadResource_success() throws Exception {
        FileAccessDTO dto = new FileAccessDTO();
        dto.setUrl("http://download");
        Mockito.when(fileAccessService.getDownloadUrl(123L)).thenReturn(dto);

        mockMvc.perform(get("/api/resources/123/download"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("http://download"))
                .andExpect(jsonPath("$.msg").value("获取下载链接成功"));
    }

    @Test
    void downloadResource_fail_invalidId() throws Exception {
        mockMvc.perform(get("/api/resources/abc/download"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("无效的 resourceId 格式"));
    }

    @Test
    void downloadResource_fail_businessException() throws Exception {
        Mockito.when(fileAccessService.getDownloadUrl(999L)).thenThrow(new BusinessException("找不到文件"));
        mockMvc.perform(get("/api/resources/999/download"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("找不到文件"));
    }

    // previewResource
    @Test
    void previewResource_success() throws Exception {
        FileAccessDTO dto = new FileAccessDTO();
        dto.setUrl("http://preview");
        Mockito.when(fileAccessService.getPreviewUrl(456L)).thenReturn(dto);

        mockMvc.perform(get("/api/resources/456/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("http://preview"))
                .andExpect(jsonPath("$.msg").value("获取预览链接成功"));
    }

    @Test
    void previewResource_fail_invalidId() throws Exception {
        mockMvc.perform(get("/api/resources/abc/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("无效的 resourceId 格式"));
    }

    @Test
    void previewResource_fail_businessException() throws Exception {
        Mockito.when(fileAccessService.getPreviewUrl(999L)).thenThrow(new BusinessException("预览失败"));
        mockMvc.perform(get("/api/resources/999/preview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("预览失败"));
    }

    // getPreviewUrl
    @Test
    void getPreviewUrl_success() throws Exception {
        FileAccessDTO dto = new FileAccessDTO();
        dto.setUrl("http://preview-url");
        Mockito.when(fileAccessService.getPreviewUrl(456L)).thenReturn(dto);

        mockMvc.perform(get("/api/resources/456/preview-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url").value("http://preview-url"))
                .andExpect(jsonPath("$.msg").value("获取预览链接成功"));
    }

    @Test
    void getPreviewUrl_fail_invalidId() throws Exception {
        mockMvc.perform(get("/api/resources/abc/preview-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("无效的 resourceId 格式"));
    }

    @Test
    void getPreviewUrl_fail_businessException() throws Exception {
        Mockito.when(fileAccessService.getPreviewUrl(999L)).thenThrow(new BusinessException("预览失败"));
        mockMvc.perform(get("/api/resources/999/preview-url"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("预览失败"));
    }
}