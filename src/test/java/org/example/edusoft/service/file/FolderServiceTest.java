package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.impl.FolderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderServiceTest {

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FolderServiceImpl folderService;

    @Test
    @DisplayName("findSectionFolder - 找到章节文件夹")
    void testFindSectionFolder_found() {
        FileInfo child = new FileInfo();
        child.setIsDir(true);
        child.setSectiondirId(100L);
        when(fileMapper.getChildren(1L)).thenReturn(Arrays.asList(child));

        FileInfo result = folderService.findSectionFolder(1L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getSectiondirId());
    }

    @Test
    @DisplayName("findSectionFolder - 未找到章节文件夹")
    void testFindSectionFolder_notFound() {
        FileInfo child = new FileInfo();
        child.setIsDir(true);
        child.setSectiondirId(101L);
        when(fileMapper.getChildren(1L)).thenReturn(Arrays.asList(child));

        FileInfo result = folderService.findSectionFolder(1L, 100L);

        assertNull(result);
    }

    @Test
    @DisplayName("findSectionFolder - 子文件夹不是目录")
    void testFindSectionFolder_notDir() {
        FileInfo child = new FileInfo();
        child.setIsDir(false);
        child.setSectiondirId(100L);
        when(fileMapper.getChildren(1L)).thenReturn(Arrays.asList(child));

        FileInfo result = folderService.findSectionFolder(1L, 100L);

        assertNull(result);
    }

    @Test
    @DisplayName("findSectionFolder - 没有子文件夹")
    void testFindSectionFolder_noChildren() {
        when(fileMapper.getChildren(1L)).thenReturn(Collections.emptyList());

        FileInfo result = folderService.findSectionFolder(1L, 100L);

        assertNull(result);
    }

    @Test
    @DisplayName("createFolder - 创建成功")
    void testCreateFolder_success() {
        when(fileMapper.existsByNameAndParent("folder", 1L)).thenReturn(false);
        doNothing().when(fileMapper).insertNode(any(FileInfo.class));

        boolean result = folderService.createFolder("folder", 10L, 1L, 2L, 3L, 4L);

        assertTrue(result);
        verify(fileMapper, times(1)).insertNode(any(FileInfo.class));
    }

    @Test
    @DisplayName("createFolder - 名称已存在")
    void testCreateFolder_nameExists() {
        when(fileMapper.existsByNameAndParent("folder", 1L)).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            folderService.createFolder("folder", 10L, 1L, 2L, 3L, 4L)
        );
        assertEquals("名称已存在", ex.getMessage());
        verify(fileMapper, never()).insertNode(any(FileInfo.class));
    }
}

