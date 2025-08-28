package org.example.edusoft.service.imports;

import org.example.edusoft.entity.imports.ImportRecord;
import java.util.List;
import java.util.Map;

public interface ImportService {
    // 统一的学生导入接口
    ImportRecord importStudents(Long classId, Long operatorId, String importType, List<Map<String, Object>> studentData);
    
    // 获取班级的导入记录
    List<ImportRecord> getImportRecords(Long classId);
    
    // 获取导入记录详情
    ImportRecord getImportRecord(Long id);
} 