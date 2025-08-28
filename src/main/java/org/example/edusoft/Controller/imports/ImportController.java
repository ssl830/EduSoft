package org.example.edusoft.controller.imports;

import jakarta.validation.Valid;
import org.example.edusoft.common.Result;
import org.example.edusoft.common.exception.ImportException;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.entity.imports.ImportRequest;
import org.example.edusoft.service.imports.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/imports")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/students")
    public Result<ImportRecord> importStudents(@RequestBody @Valid ImportRequest request) {
        try {
            ImportRecord record = importService.importStudents(
                request.getClassId(),
                request.getOperatorId(),
                request.getImportType(),
                request.getStudentData()
            );
            
            // 检查导入结果
            if (record.getFailCount() > 0) {
                return Result.error(400, record.getFailReason());
            }
            
            return Result.success(record);
        } catch (ImportException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "导入学生数据失败：" + e.getMessage());
        }
    }

    @GetMapping("/records/{classId}")
    public Result<List<ImportRecord>> getImportRecords(@PathVariable Long classId) {
        try {
            List<ImportRecord> records = importService.getImportRecords(classId);
            return Result.success(records);
        } catch (ImportException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取导入记录失败：" + e.getMessage());
        }
    }

    @GetMapping("/record/{id}")
    public Result<ImportRecord> getImportRecord(@PathVariable Long id) {
        try {
            ImportRecord record = importService.getImportRecord(id);
            return Result.success(record);
        } catch (ImportException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "获取导入记录详情失败：" + e.getMessage());
        }
    }
} 