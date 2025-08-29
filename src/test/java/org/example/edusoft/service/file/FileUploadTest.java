package org.example.edusoft.service.file;

import org.example.edusoft.common.domain.Result;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FolderService;
import org.example.edusoft.service.file.impl.FileUploadImpl;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.ai.AIServiceClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileUploadTest {

    @Mock
    private FileMapper fileMapper;
    @Mock
    private IFileStorageProvider storageProvider;
    @Mock
    private IFileStorage fileStorage; // 新增mock
    @Mock
    private FolderService folderService;
    @Mock
    private AIServiceClient aiServiceClient;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileUploadImpl fileUpload;

    @Test
    @DisplayName("uploadFile - PUBLIC可见性全部成功")
    void testUploadFile_public_success() {
        when(fileMapper.getAllClassIdsByCourseId(1L)).thenReturn(Arrays.asList(10L, 20L));
        FileInfo rootFolder = new FileInfo();
        rootFolder.setId(100L);
        when(fileMapper.getRootFolderByClassId(anyLong())).thenReturn(rootFolder);

        when(storageProvider.getStorage()).thenReturn(fileStorage);
        when(fileStorage.upload(any(), anyString(), any())).thenReturn(new FileBo());

        when(fileMapper.isDir(anyLong())).thenReturn(true);
        when(fileMapper.existsByNameAndParent(anyString(), anyLong())).thenReturn(false);
        doNothing().when(fileMapper).insertNode(any(FileInfo.class));

        Result<?> result = fileUpload.uploadFile(multipartFile, "title", 1L, -1L, "PUBLIC", 2L, "PDF");

        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMsg());
    }

    @Test
    @DisplayName("uploadFile - PUBLIC部分班级失败")
    void testUploadFile_public_partialFail() {
        when(fileMapper.getAllClassIdsByCourseId(1L)).thenReturn(Arrays.asList(10L));
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(null);
        when(folderService.createFolder(anyString(), any(), any(), any(), any(), any())).thenReturn(false);

        Result<?> result = fileUpload.uploadFile(multipartFile, "title", 1L, -1L, "PUBLIC", 2L, "PDF");

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMsg().contains("根文件夹创建失败"));
    }

    @Test
    @DisplayName("uploadFile - CLASS_ONLY正常上传")
    void testUploadFile_classOnly_success() {
        when(fileMapper.getClassIdByUserandCourse(anyLong(), anyLong())).thenReturn(10L);
        FileInfo rootFolder = new FileInfo();
        rootFolder.setId(100L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(rootFolder);

        when(storageProvider.getStorage()).thenReturn(fileStorage);
        when(fileStorage.upload(any(), anyString(), any())).thenReturn(new FileBo());

        when(fileMapper.isDir(anyLong())).thenReturn(true);
        when(fileMapper.existsByNameAndParent(anyString(), anyLong())).thenReturn(false);
        doNothing().when(fileMapper).insertNode(any(FileInfo.class));

        Result<?> result = fileUpload.uploadFile(multipartFile, "title", 1L, -1L, "CLASS_ONLY", 2L, "PDF");

        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMsg());
    }

    @Test
    @DisplayName("uploadFile - CLASS_ONLY未指定班级")
    void testUploadFile_classOnly_noClass() {
        when(fileMapper.getClassIdByUserandCourse(anyLong(), anyLong())).thenReturn(null);
        when(fileMapper.getAllClassIdsByCourseId(anyLong())).thenReturn(Collections.emptyList());

        Result<?> result = fileUpload.uploadFile(multipartFile, "title", 1L, -1L, "CLASS_ONLY", 2L, "PDF");

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMsg().contains("未指定班级"));
    }

    @Test
    @DisplayName("uploadFile - 无效可见性")
    void testUploadFile_invalidVisibility() {
        Result<?> result = fileUpload.uploadFile(multipartFile, "title", 1L, -1L, "INVALID", 2L, "PDF");
        assertNotEquals(200, result.getCode());
        assertTrue(result.getMsg().contains("无效的可见性设置"));
    }

    @Test
    @DisplayName("upload - 文件为空")
    void testUpload_emptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);

        Result<?> result = fileUpload.upload(multipartFile, "title", 1L, 1L, 1L, "PUBLIC", -1L, FileType.PDF, 2L);

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMsg().contains("上传文件为空"));
    }

    @Test
    @DisplayName("upload - 父节点不是文件夹")
    void testUpload_parentNotDir() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileMapper.isDir(1L)).thenReturn(false);

        Result<?> result = fileUpload.upload(multipartFile, "title", 1L, 1L, 1L, "PUBLIC", -1L, FileType.PDF, 2L);

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMsg().contains("父节点不是文件夹"));
    }

    @Test
    @DisplayName("upload - 正常上传")
    void testUpload_success() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(fileMapper.isDir(anyLong())).thenReturn(true);
        when(multipartFile.getOriginalFilename()).thenReturn("file.pdf");

        when(storageProvider.getStorage()).thenReturn(fileStorage);
        when(fileStorage.upload(any(), anyString(), any())).thenReturn(new FileBo());

        when(fileMapper.existsByNameAndParent(anyString(), anyLong())).thenReturn(false);
        doNothing().when(fileMapper).insertNode(any(FileInfo.class));

        Result<?> result = fileUpload.upload(multipartFile, "title", 1L, 1L, 1L, "PUBLIC", -1L, FileType.PDF, 2L);

        assertEquals(200, result.getCode());
        assertEquals("上传成功", result.getMsg());
    }
}
