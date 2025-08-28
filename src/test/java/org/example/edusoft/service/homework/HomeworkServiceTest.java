package org.example.edusoft.service.homework;

import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.dto.homework.HomeworkDTO;
import org.example.edusoft.dto.homework.HomeworkSubmissionDTO;
import org.example.edusoft.entity.file.FileAccessDTO;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.entity.homework.Homework;
import org.example.edusoft.entity.homework.HomeworkSubmission;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.homework.HomeworkMapper;
import org.example.edusoft.mapper.homework.HomeworkSubmissionMapper;
import org.example.edusoft.service.file.FileAccessService;
import org.example.edusoft.service.homework.impl.HomeworkServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeworkServiceTest {

    @Mock
    private HomeworkMapper homeworkMapper;
    @Mock
    private HomeworkSubmissionMapper submissionMapper;
    @Mock
    private IFileStorageProvider storageProvider;
    @Mock
    private IFileStorage fileStorage;
    @Mock
    private FileAccessService fileAccessService;
    @Mock
    private MultipartFile multipartFile;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private HomeworkServiceImpl homeworkService;

    @Test
    @DisplayName("createHomework - 正常创建")
    void testCreateHomework_success() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("hw.pdf");
        when(storageProvider.getStorage()).thenReturn(fileStorage);
        FileBo fileBo = mock(FileBo.class);
        when(fileBo.getFileName()).thenReturn("hw.pdf");
        when(fileBo.getUrl()).thenReturn("url");
        when(fileStorage.upload(any(), anyString(), any())).thenReturn(fileBo);
        doAnswer(invocation -> {
            Homework hw = invocation.getArgument(0);
            hw.setId(123L);
            return null;
        }).when(homeworkMapper).insert(any(Homework.class));

        Long id = homeworkService.createHomework(1L, "title", "desc", "2025-08-26 16:00:00", multipartFile);

        assertEquals(123L, id);
    }

    @Test
    @DisplayName("createHomework - 缺少参数抛异常")
    void testCreateHomework_missingParam() {
        assertThrows(BusinessException.class, () -> homeworkService.createHomework(null, null, null, null, null));
    }

    @Test
    @DisplayName("createHomework - 无附件")
    void testCreateHomework_noFile() {
        when(multipartFile.isEmpty()).thenReturn(true);
        doAnswer(invocation -> {
            Homework hw = invocation.getArgument(0);
            hw.setId(456L);
            return null;
        }).when(homeworkMapper).insert(any(Homework.class));

        Long id = homeworkService.createHomework(1L, "title", "desc", null, multipartFile);

        assertEquals(456L, id);
    }

    @Test
    @DisplayName("submitHomework - 正常提交")
    void testSubmitHomework_success() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getOriginalFilename()).thenReturn("sub.pdf");
        Homework hw = new Homework();
        hw.setDeadline(LocalDateTime.now().plusDays(1));
        when(homeworkMapper.selectById(1L)).thenReturn(hw);
        when(storageProvider.getStorage()).thenReturn(fileStorage);
        FileBo fileBo = mock(FileBo.class);
        when(fileBo.getFileName()).thenReturn("sub.pdf");
        when(fileBo.getUrl()).thenReturn("url");
        when(fileStorage.upload(any(), anyString(), any())).thenReturn(fileBo);
        doAnswer(invocation -> {
            HomeworkSubmission sub = invocation.getArgument(0);
            sub.setId(789L);
            return null;
        }).when(submissionMapper).insert(any(HomeworkSubmission.class));

        Long id = homeworkService.submitHomework(1L, 2L, multipartFile);

        assertEquals(789L, id);
    }

    @Test
    @DisplayName("submitHomework - 文件为空抛异常")
    void testSubmitHomework_emptyFile() {
        when(multipartFile.isEmpty()).thenReturn(true);
        assertThrows(BusinessException.class, () -> homeworkService.submitHomework(1L, 2L, multipartFile));
    }

    @Test
    @DisplayName("submitHomework - 作业不存在抛异常")
    void testSubmitHomework_homeworkNotExist() {
        when(multipartFile.isEmpty()).thenReturn(false);
        when(homeworkMapper.selectById(1L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> homeworkService.submitHomework(1L, 2L, multipartFile));
    }

    @Test
    @DisplayName("submitHomework - 已过截止时间抛异常")
    void testSubmitHomework_deadlinePassed() {
        when(multipartFile.isEmpty()).thenReturn(false);
        Homework hw = new Homework();
        hw.setDeadline(LocalDateTime.now().minusDays(1));
        when(homeworkMapper.selectById(1L)).thenReturn(hw);
        assertThrows(BusinessException.class, () -> homeworkService.submitHomework(1L, 2L, multipartFile));
    }

    @Test
    @DisplayName("getHomeworkList - 返回作业列表")
    void testGetHomeworkList_nonEmpty() {
        Homework hw = new Homework();
        hw.setId(1L);
        hw.setTitle("t");
        hw.setDescription("d");
        hw.setObjectName("obj");
        hw.setDeadline(LocalDateTime.now());
        when(homeworkMapper.selectByClassId(1L)).thenReturn(Arrays.asList(hw));
        FileAccessDTO fileAccessDTO = mock(FileAccessDTO.class);
        when(fileAccessDTO.getUrl()).thenReturn("url");
        when(fileAccessService.getDownloadUrlByObjectName("obj")).thenReturn(fileAccessDTO);

        List<HomeworkDTO> list = homeworkService.getHomeworkList(1L);

        assertEquals(1, list.size());
        assertEquals("t", list.get(0).getTitle());
    }

    @Test
    @DisplayName("getHomeworkList - 空列表")
    void testGetHomeworkList_empty() {
        when(homeworkMapper.selectByClassId(1L)).thenReturn(Collections.emptyList());
        List<HomeworkDTO> list = homeworkService.getHomeworkList(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("getSubmissionList - 返回提交列表")
    void testGetSubmissionList_nonEmpty() {
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setId(1L);
        sub.setStudentId(2L);
        sub.setStudentName("stu");
        sub.setObjectName("obj");
        sub.setSubmittedAt(LocalDateTime.now());
        when(submissionMapper.selectByHomeworkId(1L)).thenReturn(Arrays.asList(sub));
        FileAccessDTO fileAccessDTO = mock(FileAccessDTO.class);
        when(fileAccessDTO.getUrl()).thenReturn("url");
        when(fileAccessService.getDownloadUrlByObjectName("obj")).thenReturn(fileAccessDTO);

        List<HomeworkSubmissionDTO> list = homeworkService.getSubmissionList(1L);

        assertEquals(1, list.size());
        assertEquals("stu", list.get(0).getStudentName());
    }

    @Test
    @DisplayName("getSubmissionList - 空列表")
    void testGetSubmissionList_empty() {
        when(submissionMapper.selectByHomeworkId(1L)).thenReturn(Collections.emptyList());
        List<HomeworkSubmissionDTO> list = homeworkService.getSubmissionList(1L);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("deleteHomework - 正常删除")
    void testDeleteHomework_success() {
        Homework hw = new Homework();
        hw.setId(1L);
        hw.setObjectName("obj");
        when(homeworkMapper.selectById(1L)).thenReturn(hw);
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setObjectName("subObj");
        when(submissionMapper.selectByHomeworkId(1L)).thenReturn(Arrays.asList(sub));
        when(storageProvider.getStorage()).thenReturn(fileStorage);

        homeworkService.deleteHomework(1L);

        verify(fileStorage, times(1)).delete("obj");
        verify(fileStorage, times(1)).delete("subObj");
        verify(submissionMapper, times(1)).deleteByHomeworkId(1L);
        verify(homeworkMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteHomework - 作业不存在")
    void testDeleteHomework_notExist() {
        when(homeworkMapper.selectById(2L)).thenReturn(null);
        homeworkService.deleteHomework(2L);
        verify(homeworkMapper, never()).deleteById(2L);
    }

    @Test
    @DisplayName("getHomework - 正常获取")
    void testGetHomework_found() {
        Homework hw = new Homework();
        hw.setId(1L);
        hw.setTitle("t");
        hw.setDescription("d");
        hw.setObjectName("obj");
        hw.setDeadline(LocalDateTime.now());
        when(homeworkMapper.selectById(1L)).thenReturn(hw);
        FileAccessDTO fileAccessDTO = mock(FileAccessDTO.class);
        when(fileAccessDTO.getUrl()).thenReturn("url");
        when(fileAccessService.getDownloadUrl(1L)).thenReturn(fileAccessDTO);

        HomeworkDTO dto = homeworkService.getHomework(1L);

        assertNotNull(dto);
        assertEquals("t", dto.getTitle());
    }

    @Test
    @DisplayName("getHomework - 未找到")
    void testGetHomework_notFound() {
        when(homeworkMapper.selectById(2L)).thenReturn(null);
        HomeworkDTO dto = homeworkService.getHomework(2L);
        assertNull(dto);
    }

    @Test
    @DisplayName("getStudentSubmission - 正常获取")
    void testGetStudentSubmission_found() {
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setId(1L);
        sub.setStudentId(2L);
        sub.setObjectName("obj");
        sub.setSubmittedAt(LocalDateTime.now());
        when(submissionMapper.selectByHomeworkAndStudent(1L, 2L)).thenReturn(sub);
        FileAccessDTO fileAccessDTO = mock(FileAccessDTO.class);
        when(fileAccessDTO.getUrl()).thenReturn("url");
        when(fileAccessService.getDownloadUrlByObjectName("obj")).thenReturn(fileAccessDTO);

        HomeworkSubmissionDTO dto = homeworkService.getStudentSubmission(1L, 2L);

        assertNotNull(dto);
        assertEquals("obj", dto.getFileName());
    }

    @Test
    @DisplayName("getStudentSubmission - 未找到")
    void testGetStudentSubmission_notFound() {
        when(submissionMapper.selectByHomeworkAndStudent(1L, 2L)).thenReturn(null);
        HomeworkSubmissionDTO dto = homeworkService.getStudentSubmission(1L, 2L);
        assertNull(dto);
    }

    @Test
    @DisplayName("downloadHomeworkFile - 正常下载")
    void testDownloadHomeworkFile_success() {
        Homework hw = new Homework();
        hw.setId(1L);
        hw.setObjectName("obj");
        when(homeworkMapper.selectById(1L)).thenReturn(hw);
        when(storageProvider.getStorage()).thenReturn(fileStorage);

        homeworkService.downloadHomeworkFile(1L, response);

        verify(fileStorage, times(1)).download("obj", response);
    }

    @Test
    @DisplayName("downloadHomeworkFile - 作业不存在抛异常")
    void testDownloadHomeworkFile_notExist() {
        when(homeworkMapper.selectById(2L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> homeworkService.downloadHomeworkFile(2L, response));
    }

    @Test
    @DisplayName("downloadSubmissionFile - 正常下载")
    void testDownloadSubmissionFile_success() {
        HomeworkSubmission sub = new HomeworkSubmission();
        sub.setId(1L);
        sub.setObjectName("obj");
        when(submissionMapper.selectById(1L)).thenReturn(sub);
        when(storageProvider.getStorage()).thenReturn(fileStorage);

        homeworkService.downloadSubmissionFile(1L, response);

        verify(fileStorage, times(1)).download("obj", response);
    }

    @Test
    @DisplayName("downloadSubmissionFile - 提交不存在抛异常")
    void testDownloadSubmissionFile_notExist() {
        when(submissionMapper.selectById(2L)).thenReturn(null);
        assertThrows(BusinessException.class, () -> homeworkService.downloadSubmissionFile(2L, response));
    }
}
