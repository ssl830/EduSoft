package org.example.edusoft.service.imports.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.mapper.classroom.ClassUserMapper;
import org.example.edusoft.mapper.imports.ImportRecordMapper;
import org.example.edusoft.service.imports.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private ImportRecordMapper importRecordMapper;

    @Autowired
    private ClassUserMapper classUserMapper;

    @Override
    @Transactional
    public ImportRecord importStudents(Long classId, Long operatorId, String importType, List<Map<String, Object>> studentData) {
        ImportRecord record = new ImportRecord();
        record.setClassId(classId);
        record.setOperatorId(operatorId);
        record.setImportTime(LocalDateTime.now());
        record.setImportType(importType);
        
        int totalCount = studentData.size();
        int successCount = 0;
        int failCount = 0;
        List<String> failReasons = new ArrayList<>();

        for (Map<String, Object> student : studentData) {
            try {
                // 从JSON数据中获取学生ID
                Long studentId = Long.parseLong(student.get("student_id").toString());
                
                // 检查学生是否已在班级中
                if (classUserMapper.checkUserInClass(classId, studentId) == 0) {
                    ClassUser classUser = new ClassUser();
                    classUser.setClassId(classId);
                    classUser.setUserId(studentId);
                    classUser.setJoinedAt(LocalDateTime.now());
                    classUserMapper.insert(classUser);
                    successCount++;
                } else {
                    failCount++;
                    failReasons.add("学生ID " + studentId + " 已在班级中");
                }
            } catch (Exception e) {
                failCount++;
                failReasons.add("学生数据处理失败: " + e.getMessage());
            }
        }

        record.setTotalCount(totalCount);
        record.setSuccessCount(successCount);
        record.setFailCount(failCount);
        record.setFailReason(String.join("; ", failReasons));
        
        importRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<ImportRecord> getImportRecords(Long classId) {
        return importRecordMapper.getImportRecordsByClassId(classId);
    }

    @Override
    public ImportRecord getImportRecord(Long id) {
        return importRecordMapper.selectById(id);
    }
} 