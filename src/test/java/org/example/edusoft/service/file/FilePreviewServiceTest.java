package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.service.file.impl.FilePreviewServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilePreviewServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private IFileStorageProvider storageProvider;

    @Mock
    private IFileStorage storage;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream servletOutputStream;

    @InjectMocks
    private FilePreviewServiceImpl filePreviewService;

    @Test
    @DisplayName("previewFile - 文件存在且为文件")
    void testPreviewFile_fileExists() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        fileInfo.setIsDir(false);
        fileInfo.setObjectName("test.txt");
        fileInfo.setName("test.txt");
        when(fileMapper.selectById(1L)).thenReturn(fileInfo);
        when(storageProvider.getStorage()).thenReturn(storage);
        when(response.getOutputStream()).thenReturn(servletOutputStream);

        assertDoesNotThrow(() -> filePreviewService.previewFile(1L, response));
        verify(storage, times(1)).download(eq("test.txt"), any(ServletOutputStream.class));
    }

    @Test
    @DisplayName("previewFile - 文件不存在")
    void testPreviewFile_fileNotExists() {
        when(fileMapper.selectById(2L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> filePreviewService.previewFile(2L, response));
        assertTrue(ex.getMessage().contains("仅支持预览单个文件"));
    }

    @Test
    @DisplayName("previewFile - 是文件夹")
    void testPreviewFile_isDir() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(3L);
        fileInfo.setIsDir(true);
        when(fileMapper.selectById(3L)).thenReturn(fileInfo);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> filePreviewService.previewFile(3L, response));
        assertTrue(ex.getMessage().contains("仅支持预览单个文件"));
    }

    @Test
    @DisplayName("previewFile - IO异常")
    void testPreviewFile_ioException() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(4L);
        fileInfo.setIsDir(false);
        fileInfo.setObjectName("test.txt");
        fileInfo.setName("test.txt");
        when(fileMapper.selectById(4L)).thenReturn(fileInfo);
        when(storageProvider.getStorage()).thenReturn(storage);
        when(response.getOutputStream()).thenThrow(new IOException("IO error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> filePreviewService.previewFile(4L, response));
        assertTrue(ex.getMessage().contains("文件预览失败"));
    }
}
