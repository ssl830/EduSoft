package org.example.edusoft.service.imports;

import org.example.edusoft.common.exception.ImportException;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.mapper.classroom.ClassUserMapper;
import org.example.edusoft.mapper.imports.ImportRecordMapper;
import org.example.edusoft.mapper.user.UserMapper;
import org.example.edusoft.service.imports.impl.ImportServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceTest {

    @Mock
    private ImportRecordMapper importRecordMapper;
    @Mock
    private ClassUserMapper classUserMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ImportServiceImpl importService;

    @Test
    @DisplayName("importStudents - 正常导入")
    void testImportStudents_success() {
        Map<String, Object> student = new HashMap<>();
        student.put("student_id", "stu001");
        List<Map<String, Object>> studentData = Collections.singletonList(student);

        User user = new User();
        user.setId(10L);
        when(userMapper.findByUserId("stu001")).thenReturn(user);
        when(classUserMapper.checkUserInClass(1L, 10L)).thenReturn(0);
        when(classUserMapper.insert(any(ClassUser.class))).thenReturn(1);
        when(importRecordMapper.insert(any(ImportRecord.class))).thenReturn(1);

        ImportRecord record = importService.importStudents(1L, 2L, "FILE", studentData);

        assertEquals(1, record.getSuccessCount());
        assertEquals(0, record.getFailCount());
        assertNull(record.getFailReason());
    }

    @Test
    @DisplayName("importStudents - 参数不完整抛异常")
    void testImportStudents_paramMissing() {
        assertThrows(ImportException.class, () -> importService.importStudents(null, null, null, null));
    }

    @Test
    @DisplayName("importStudents - 导入类型错误")
    void testImportStudents_typeError() {
        List<Map<String, Object>> studentData = Collections.emptyList();
        assertThrows(ImportException.class, () -> importService.importStudents(1L, 2L, "WRONG", studentData));
    }

    @Test
    @DisplayName("importStudents - 学生数据缺少student_id字段")
    void testImportStudents_missingStudentId() {
        Map<String, Object> student = new HashMap<>();
        List<Map<String, Object>> studentData = Collections.singletonList(student);

        ImportRecord record = importService.importStudents(1L, 2L, "FILE", studentData);

        assertEquals(0, record.getSuccessCount());
        assertEquals(1, record.getFailCount());
        assertTrue(record.getFailReason().contains("student_id字段"));
    }

    @Test
    @DisplayName("importStudents - 学号不存在")
    void testImportStudents_userNotExist() {
        Map<String, Object> student = new HashMap<>();
        student.put("student_id", "stu002");
        List<Map<String, Object>> studentData = Collections.singletonList(student);

        when(userMapper.findByUserId("stu002")).thenReturn(null);

        ImportRecord record = importService.importStudents(1L, 2L, "FILE", studentData);

        assertEquals(0, record.getSuccessCount());
        assertEquals(1, record.getFailCount());
        assertTrue(record.getFailReason().contains("用户不存在"));
    }

    @Test
    @DisplayName("importStudents - 学生已在班级中")
    void testImportStudents_alreadyInClass() {
        Map<String, Object> student = new HashMap<>();
        student.put("student_id", "stu003");
        List<Map<String, Object>> studentData = Collections.singletonList(student);

        User user = new User();
        user.setId(11L);
        when(userMapper.findByUserId("stu003")).thenReturn(user);
        when(classUserMapper.checkUserInClass(1L, 11L)).thenReturn(1);

        ImportRecord record = importService.importStudents(1L, 2L, "FILE", studentData);

        assertEquals(0, record.getSuccessCount());
        assertEquals(1, record.getFailCount());
        assertTrue(record.getFailReason().contains("已在班级中"));
    }

    @Test
    @DisplayName("getImportRecords - 正常获取")
    void testGetImportRecords_success() {
        ImportRecord record = new ImportRecord();
        record.setId(1L);
        when(importRecordMapper.getImportRecordsByClassId(1L)).thenReturn(Arrays.asList(record));

        List<ImportRecord> result = importService.getImportRecords(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("getImportRecords - classId为空抛异常")
    void testGetImportRecords_nullClassId() {
        assertThrows(ImportException.class, () -> importService.getImportRecords(null));
    }

    @Test
    @DisplayName("getImportRecord - 正常获取")
    void testGetImportRecord_success() {
        ImportRecord record = new ImportRecord();
        record.setId(2L);
        when(importRecordMapper.selectById(2L)).thenReturn(record);

        ImportRecord result = importService.getImportRecord(2L);

        assertEquals(2L, result.getId());
    }

    @Test
    @DisplayName("getImportRecord - id为空抛异常")
    void testGetImportRecord_nullId() {
        assertThrows(ImportException.class, () -> importService.getImportRecord(null));
    }

    @Test
    @DisplayName("getImportRecord - 记录不存在")
    void testGetImportRecord_notExist() {
        when(importRecordMapper.selectById(3L)).thenReturn(null);

        assertThrows(ImportException.class, () -> importService.getImportRecord(3L));
    }
}
