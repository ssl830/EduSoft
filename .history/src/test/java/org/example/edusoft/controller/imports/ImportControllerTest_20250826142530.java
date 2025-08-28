package org.example.edusoft.controller.imports;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.edusoft.common.Result;
import org.example.edusoft.common.exception.ImportException;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.entity.imports.ImportRequest;
import org.example.edusoft.service.imports.ImportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImportController.class)
public class ImportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImportService importService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    ---

    ## importStudents() 方法的测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证当导入服务成功且无失败记录时，控制器返回成功结果和 `200 OK` 状态码。
    ```java
    @Test
    @DisplayName("should return success when importing students with no failures")
    void importStudents_Success() throws Exception {
        // 1. 准备请求和模拟的成功响应数据
        ImportRequest request = new ImportRequest();
        request.setClassId(1L);
        request.setOperatorId(101L);
        request.setImportType(1);
        request.setStudentData(Collections.singletonList("student1,12345"));

        ImportRecord record = new ImportRecord();
        record.setId(1L);
        record.setFailCount(0);
        record.setSuccessCount(1);
        record.setFailReason("");

        // 2. 模拟 service 层的行为
        when(importService.importStudents(any(), any(), any(), any()))
                .thenReturn(record);

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/imports/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.failCount").value(0))
                .andExpect(jsonPath("$.data.successCount").value(1));
    }
    ```

    ### 失败情况测试 (Negative Tests)
    **目标:** 验证控制器在处理不同类型的失败时，能返回正确的错误码和错误信息。
    ```java
    @Test
    @DisplayName("should return error when service returns a record with failures")
    void importStudents_RecordWithFailures() throws Exception {
        // 1. 准备请求和模拟的失败响应数据
        ImportRequest request = new ImportRequest();
        request.setClassId(1L);

        ImportRecord record = new ImportRecord();
        record.setFailCount(1);
        record.setFailReason("无效的学生数据：studentA,abcde");

        // 2. 模拟 service 层的行为
        when(importService.importStudents(any(), any(), any(), any()))
                .thenReturn(record);

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/imports/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // 注意：控制器逻辑返回200，但Result的code是400
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("无效的学生数据：studentA,abcde"));
    }

    @Test
    @DisplayName("should return error when service throws a known ImportException")
    void importStudents_ImportException() throws Exception {
        // 1. 准备请求
        ImportRequest request = new ImportRequest();
        request.setClassId(1L);

        // 2. 模拟 service 抛出特定异常
        when(importService.importStudents(any(), any(), any(), any()))
                .thenThrow(new ImportException(401, "班级ID不存在"));

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/imports/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.msg").value("班级ID不存在"));
    }

    @Test
    @DisplayName("should return 500 when an unknown exception occurs")
    void importStudents_GenericException() throws Exception {
        // 1. 准备请求
        ImportRequest request = new ImportRequest();
        request.setClassId(1L);

        // 2. 模拟 service 抛出未知异常
        when(importService.importStudents(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("数据库连接失败"));

        // 3. 执行请求并验证结果
        mockMvc.perform(post("/api/imports/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("导入学生数据失败：数据库连接失败"));
    }
    ```

    ---

    ## getImportRecords() 方法的测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证当服务层成功返回导入记录列表时，控制器返回成功结果和 `200 OK` 状态码。
    ```java
    @Test
    @DisplayName("should return a list of import records for a class")
    void getImportRecords_Success() throws Exception {
        // 1. 准备模拟数据
        Long classId = 1L;
        List<ImportRecord> records = List.of(new ImportRecord(), new ImportRecord());

        // 2. 模拟 service 层的行为
        when(importService.getImportRecords(classId)).thenReturn(records);

        // 3. 执行请求并验证结果
        mockMvc.perform(get("/api/imports/records/{classId}", classId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(2));
    }
    ```

    ### 失败情况测试 (Negative Tests)
    **目标:** 验证当服务层抛出异常时，控制器能返回正确的错误信息。
    ```java
    @Test
    @DisplayName("should return error when a known exception occurs getting records")
    void getImportRecords_ImportException() throws Exception {
        // 1. 准备模拟异常
        Long classId = 99L;
        when(importService.getImportRecords(classId))
                .thenThrow(new ImportException(404, "班级无导入记录"));

        // 2. 执行请求并验证结果
        mockMvc.perform(get("/api/imports/records/{classId}", classId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.msg").value("班级无导入记录"));
    }
    ```

    ---

    ## getImportRecord() 方法的测试

    ### 正常情况测试 (Positive Test)
    **目标:** 验证当服务层成功返回单个导入记录时，控制器返回成功结果和 `200 OK` 状态码。
    ```java
    @Test
    @DisplayName("should return a single import record by id")
    void getImportRecord_Success() throws Exception {
        // 1. 准备模拟数据
        Long recordId = 123L;
        ImportRecord record = new ImportRecord();
        record.setId(recordId);
        record.setSuccessCount(50);

        // 2. 模拟 service 层的行为
        when(importService.getImportRecord(recordId)).thenReturn(record);

        // 3. 执行请求并验证结果
        mockMvc.perform(get("/api/imports/record/{id}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(123))
                .andExpect(jsonPath("$.data.successCount").value(50));
    }
    ```

    ### 失败情况测试 (Negative Tests)
    **目标:** 验证当服务层因记录不存在等原因抛出异常时，控制器能返回正确的错误信息。
    ```java
    @Test
    @DisplayName("should return error when record with id does not exist")
    void getImportRecord_ImportException() throws Exception {
        // 1. 准备模拟异常
        Long recordId = 999L;
        when(importService.getImportRecord(recordId))
                .thenThrow(new ImportException(404, "导入记录不存在"));

        // 2. 执行请求并验证结果
        mockMvc.perform(get("/api/imports/record/{id}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.msg").value("导入记录不存在"));
    }