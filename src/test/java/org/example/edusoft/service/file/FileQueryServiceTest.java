package org.example.edusoft.service.file;

import org.example.edusoft.common.dto.file.FileResponseDTO;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.impl.FileQueryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileQueryServiceTest {

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileQueryServiceImpl fileQueryService;

    @Test
    @DisplayName("getAllFilesByUserId - 返回非空文件列表")
    void testGetAllFilesByUserId_nonEmpty() {
        FileInfo file1 = new FileInfo();
        FileInfo file2 = new FileInfo();
        when(fileMapper.getRootFoldersByUserId(1L)).thenReturn(Arrays.asList(file1, file2));

        List<FileResponseDTO> result = fileQueryService.getAllFilesByUserId(1L);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getAllFilesByUserId - 返回空列表")
    void testGetAllFilesByUserId_empty() {
        when(fileMapper.getRootFoldersByUserId(2L)).thenReturn(Collections.emptyList());

        List<FileResponseDTO> result = fileQueryService.getAllFilesByUserId(2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getFilesByUserandCourse - 正常返回文件列表")
    void testGetFilesByUserandCourse_found() {
        when(fileMapper.getClassIdByUserandCourse(1L, 2L)).thenReturn(10L);
        FileInfo root = new FileInfo();
        root.setId(100L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(root);
        FileInfo child = new FileInfo();
        when(fileMapper.getChildren(100L)).thenReturn(Arrays.asList(child));

        List<FileResponseDTO> result = fileQueryService.getFilesByUserandCourse(1L, 2L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getFilesByUserandCourse - 未找到根文件夹")
    void testGetFilesByUserandCourse_notFound() {
        when(fileMapper.getClassIdByUserandCourse(1L, 2L)).thenReturn(10L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(null);

        List<FileResponseDTO> result = fileQueryService.getFilesByUserandCourse(1L, 2L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getFilesByUserandCourseWithFilter - 老师获取课程文件")
    void testGetFilesByUserandCourseWithFilter_teacher() {
        FileInfo file = new FileInfo();
        when(fileMapper.getFilesByCourseId(anyLong(), anyString(), anyString(), any(), anyString()))
            .thenReturn(Arrays.asList(file));

        List<FileResponseDTO> result = fileQueryService.getFilesByUserandCourseWithFilter(
            1L, 2L, "test", "PDF", null, true);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getFilesByUserandCourseWithFilter - 学生未找到根文件夹")
    void testGetFilesByUserandCourseWithFilter_student_noRoot() {
        when(fileMapper.getClassIdByUserandCourse(anyLong(), anyLong())).thenReturn(10L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(null);

        List<FileResponseDTO> result = fileQueryService.getFilesByUserandCourseWithFilter(
            1L, 2L, "test", "PDF", null, false);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getFilesByUserandCourseWithFilter - 学生获取班级文件")
    void testGetFilesByUserandCourseWithFilter_student_found() {
        when(fileMapper.getClassIdByUserandCourse(anyLong(), anyLong())).thenReturn(10L);
        FileInfo root = new FileInfo();
        root.setId(100L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(root);
        FileInfo file = new FileInfo();
        when(fileMapper.getFilesByClassId(anyLong(), anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(file));

        List<FileResponseDTO> result = fileQueryService.getFilesByUserandCourseWithFilter(
            1L, 2L, "test", "PDF", null, false);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getChildrenFiles - 有效文件夹ID")
    void testGetChildrenFiles_validFolder() {
        FileInfo folder = new FileInfo();
        folder.setIsDir(true);
        when(fileMapper.selectById(1L)).thenReturn(folder);
        FileInfo child = new FileInfo();
        when(fileMapper.getChildren(1L)).thenReturn(Arrays.asList(child));

        List<FileResponseDTO> result = fileQueryService.getChildrenFiles(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getChildrenFiles - 无效文件夹ID")
    void testGetChildrenFiles_invalidFolder() {
        FileInfo folder = new FileInfo();
        folder.setIsDir(false);
        when(fileMapper.selectById(2L)).thenReturn(folder);

        assertThrows(IllegalArgumentException.class, () -> fileQueryService.getChildrenFiles(2L));
    }

    @Test
    @DisplayName("getChildrenFiles - 文件夹不存在")
    void testGetChildrenFiles_folderNotExist() {
        when(fileMapper.selectById(3L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> fileQueryService.getChildrenFiles(3L));
    }

    @Test
    @DisplayName("getFileVersions - 正常返回版本列表")
    void testGetFileVersions_found() {
        when(fileMapper.getClassIdByUserandCourse(1L, 2L)).thenReturn(10L);
        FileInfo root = new FileInfo();
        root.setId(100L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(root);
        FileInfo version = new FileInfo();
        when(fileMapper.getVersionsByBaseName("base", 100L)).thenReturn(Arrays.asList(version));

        List<FileResponseDTO> result = fileQueryService.getFileVersions(1L, 2L, "base");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getFileVersions - 未找到根文件夹")
    void testGetFileVersions_noRoot() {
        when(fileMapper.getClassIdByUserandCourse(1L, 2L)).thenReturn(10L);
        when(fileMapper.getRootFolderByClassId(10L)).thenReturn(null);

        List<FileResponseDTO> result = fileQueryService.getFileVersions(1L, 2L, "base");

        assertTrue(result.isEmpty());
    }
}

