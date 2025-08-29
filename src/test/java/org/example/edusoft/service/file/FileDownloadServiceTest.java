package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.service.file.impl.FileDownloadServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletResponse;

import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloadServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private IFileStorageProvider storageProvider;

    @Mock
    private IFileStorage storage;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private FileDownloadServiceImpl fileDownloadService;

    @Test
    @DisplayName("downloadFileOrFolder - 文件存在且为文件")
    void testDownloadFileOrFolder_fileExists() throws Exception {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        fileInfo.setIsDir(false);
        fileInfo.setName("test.txt");
        fileInfo.setObjectName("test.txt");
        when(fileMapper.selectById(1L)).thenReturn(fileInfo);
        when(storageProvider.getStorage()).thenReturn(storage);

        doNothing().when(storage).download(eq("test.txt"), any(HttpServletResponse.class));

        assertDoesNotThrow(() -> fileDownloadService.downloadFileOrFolder(1L, response));
        verify(storage, times(1)).download(eq("test.txt"), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("downloadFileOrFolder - 文件不存在")
    void testDownloadFileOrFolder_fileNotExists() {
        when(fileMapper.selectById(2L)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> fileDownloadService.downloadFileOrFolder(2L, response));
        assertTrue(ex.getMessage().contains("文件不存在"));
    }
}

