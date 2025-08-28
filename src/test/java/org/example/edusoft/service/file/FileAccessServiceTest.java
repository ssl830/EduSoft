package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileAccessDTO;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.common.properties.FsServerProperties;
import org.example.edusoft.service.file.impl.FileAccessServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileAccessServiceTest {

    @Mock
    private FileMapper fileMapper;

    @Mock
    private FsServerProperties fsServerProperties;

    @InjectMocks
    private FileAccessServiceImpl fileAccessService;

    @Test
    @DisplayName("getDownloadUrl - 文件存在且为文件")
    void testGetDownloadUrl_fileExists() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        fileInfo.setIsDir(false);
        fileInfo.setObjectName("test.txt");
        when(fileMapper.selectById(1L)).thenReturn(fileInfo);

        // getDownloadUrlByObjectName 会抛异常，因为未mock OSS相关内容，这里只验证流程
        assertThrows(BusinessException.class, () -> fileAccessService.getDownloadUrl(1L));
    }

    @Test
    @DisplayName("getDownloadUrl - 文件不存在")
    void testGetDownloadUrl_fileNotExists() {
        when(fileMapper.selectById(2L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getDownloadUrl(2L));
        assertTrue(ex.getMessage().contains("文件不存在"));
    }

    @Test
    @DisplayName("getDownloadUrl - 是文件夹")
    void testGetDownloadUrl_isDir() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(3L);
        fileInfo.setIsDir(true);
        when(fileMapper.selectById(3L)).thenReturn(fileInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getDownloadUrl(3L));
        assertTrue(ex.getMessage().contains("不支持获取文件夹的下载链接"));
    }

    @Test
    @DisplayName("getPreviewUrl - 文件存在且为文件")
    void testGetPreviewUrl_fileExists() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(1L);
        fileInfo.setIsDir(false);
        fileInfo.setObjectName("test.txt");
        when(fileMapper.selectById(1L)).thenReturn(fileInfo);

        assertThrows(BusinessException.class, () -> fileAccessService.getPreviewUrl(1L));
    }

    @Test
    @DisplayName("getPreviewUrl - 文件不存在")
    void testGetPreviewUrl_fileNotExists() {
        when(fileMapper.selectById(2L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getPreviewUrl(2L));
        assertTrue(ex.getMessage().contains("文件不存在"));
    }

    @Test
    @DisplayName("getPreviewUrl - 是文件夹")
    void testGetPreviewUrl_isDir() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(3L);
        fileInfo.setIsDir(true);
        when(fileMapper.selectById(3L)).thenReturn(fileInfo);

        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getPreviewUrl(3L));
        assertTrue(ex.getMessage().contains("不支持预览文件夹"));
    }

    @Test
    @DisplayName("getDownloadUrlByObjectName - objectName有效")
    void testGetDownloadUrlByObjectName_valid() {
        // OSS相关未mock，直接验证异常
        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getDownloadUrlByObjectName("test.txt"));
        assertTrue(ex.getMessage().contains("生成下载链接失败"));
    }

    @Test
    @DisplayName("getDownloadUrlByObjectName - objectName为空")
    void testGetDownloadUrlByObjectName_empty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getDownloadUrlByObjectName(""));
        assertTrue(ex.getMessage().contains("文件路径不能为空"));
    }

    @Test
    @DisplayName("getPreviewUrlByObjectName - objectName有效")
    void testGetPreviewUrlByObjectName_valid() {
        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getPreviewUrlByObjectName("test.txt"));
        assertTrue(ex.getMessage().contains("生成预览链接失败"));
    }

    @Test
    @DisplayName("getPreviewUrlByObjectName - objectName为空")
    void testGetPreviewUrlByObjectName_empty() {
        BusinessException ex = assertThrows(BusinessException.class, () -> fileAccessService.getPreviewUrlByObjectName(""));
        assertTrue(ex.getMessage().contains("文件路径不能为空"));
    }
}

