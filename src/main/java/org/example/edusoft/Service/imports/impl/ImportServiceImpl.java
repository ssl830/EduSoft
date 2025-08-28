package org.example.edusoft.service.imports.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.common.exception.ImportException;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.mapper.classroom.ClassUserMapper;
import org.example.edusoft.mapper.imports.ImportRecordMapper;
import org.example.edusoft.mapper.user.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public ImportRecord importStudents(Long classId, Long operatorId, String importType, List<Map<String, Object>> studentData) {
        if (classId == null || operatorId == null || importType == null || studentData == null) {
            throw new ImportException(400, "参数不完整，请检查classId、operatorId、importType和studentData是否都已提供");
        }

        if (!"FILE".equals(importType) && !"MANUAL".equals(importType)) {
            throw new ImportException(400, "导入类型不正确，必须是FILE或MANUAL");
        }

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
                if (!student.containsKey("student_id")) {
                    throw new ImportException(400, "学生数据缺少student_id字段");
                }

                String studentUserId = student.get("student_id").toString();
                
                // 通过学号查找用户
                User user = userMapper.findByUserId(studentUserId);
                if (user == null) {
                    failCount++;
                    failReasons.add(String.format("学号 %s 对应的用户不存在", studentUserId));
                    continue;
                }

                // 检查学生是否已在班级中
                if (classUserMapper.checkUserInClass(classId, user.getId()) > 0) {
                    failCount++;
                    failReasons.add(String.format("学号 %s 的学生已在班级中", studentUserId));
                    continue;
                }

                // 添加学生到班级
                ClassUser classUser = new ClassUser();
                classUser.setClassId(classId);
                classUser.setUserId(user.getId());
                classUser.setJoinedAt(LocalDateTime.now());
                classUserMapper.insert(classUser);
                successCount++;
            } catch (ImportException e) {
                failCount++;
                failReasons.add(e.getMessage());
            } catch (Exception e) {
                failCount++;
                failReasons.add(String.format("处理学生数据时发生错误: %s", e.getMessage()));
            }
        }

        record.setTotalCount(totalCount);
        record.setSuccessCount(successCount);
        record.setFailCount(failCount);
        
        // 优化错误信息的格式
        if (!failReasons.isEmpty()) {
            String errorMessage = String.join("; ", failReasons);
            if (failCount == totalCount) {
                record.setFailReason("导入失败：" + errorMessage);
            } else {
                record.setFailReason("部分导入失败：" + errorMessage);
            }
        } else {
            record.setFailReason(null);
        }
        
        importRecordMapper.insert(record);
        return record;
    }

    @Override
    public List<ImportRecord> getImportRecords(Long classId) {
        if (classId == null) {
            throw new ImportException(400, "班级ID不能为空");
        }
        return importRecordMapper.getImportRecordsByClassId(classId);
    }

    @Override
    public ImportRecord getImportRecord(Long id) {
        if (id == null) {
            throw new ImportException(400, "记录ID不能为空");
        }
        ImportRecord record = importRecordMapper.selectById(id);
        if (record == null) {
            throw new ImportException(404, "导入记录不存在");
        }
        return record;
    }
} 