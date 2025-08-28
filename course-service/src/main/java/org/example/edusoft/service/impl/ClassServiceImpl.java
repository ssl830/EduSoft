package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Class;
import org.example.edusoft.dto.ClassDetailDTO;
import org.example.edusoft.entity.ClassUser;
import org.example.edusoft.entity.ImportRecord;
import org.example.edusoft.mapper.ClassMapper;
import org.example.edusoft.mapper.ClassUserMapper;
import org.example.edusoft.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ClassUserMapper classUserMapper;

    @Override
    @Transactional
    public Class createClass(Class clazz) {
        // 验证班级代码唯一性
        if (isClassCodeExists(clazz.getClassCode())) {
            throw new IllegalArgumentException("班级代码已存在");
        }
        
        // 验证班级名称
        if (!StringUtils.hasText(clazz.getName())) {
            throw new IllegalArgumentException("班级名称不能为空");
        }
        
        // 验证课程ID
        if (clazz.getCourseId() == null) {
            throw new IllegalArgumentException("课程ID不能为空");
        }
        
        classMapper.insert(clazz);
        return clazz;
    }

    @Override
    public List<Class> getClassesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new IllegalArgumentException("教师ID不能为空");
        }
        return classMapper.getClassesByTeacherId(teacherId);
    }

    @Override
    public List<Class> getClassesByStudentId(Long studentId) {
        if (studentId == null) {
            throw new IllegalArgumentException("学生ID不能为空");
        }
        return classMapper.getClassesByStudentId(studentId);
    }

    @Override
    public Class getClassById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        return clazz;
    }

    @Override
    public ClassDetailDTO getClassDetailById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return classMapper.getClassDetailById(id);
    }

    @Override
    @Transactional
    public Class updateClass(Class clazz) {
        if (clazz.getId() == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        // 检查班级是否存在
        Class existingClass = classMapper.selectById(clazz.getId());
        if (existingClass == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        // 如果修改了班级代码，需要检查唯一性
        if (!existingClass.getClassCode().equals(clazz.getClassCode()) 
            && isClassCodeExists(clazz.getClassCode())) {
            throw new IllegalArgumentException("班级代码已存在");
        }
        
        classMapper.updateById(clazz);
        return clazz;
    }

    @Override
    @Transactional
    public boolean deleteClass(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        
        // 检查班级是否存在
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        // TODO: 检查是否有关联的学生
        // 这里可以添加检查逻辑，如果有关联数据则抛出异常
        
        return classMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean joinClass(Long classId, Long userId) {
        if (classId == null || userId == null) {
            throw new IllegalArgumentException("班级ID和用户ID不能为空");
        }
        
        // 检查班级是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        // 检查用户是否已经在班级中
        if (classUserMapper.isUserInClass(classId, userId) > 0) {
            throw new IllegalArgumentException("用户已在班级中");
        }
        
        return classUserMapper.joinClass(classId, userId) > 0;
    }

    @Override
    @Transactional
    public boolean leaveClass(Long classId, Long userId) {
        if (classId == null || userId == null) {
            throw new IllegalArgumentException("班级ID和用户ID不能为空");
        }
        
        return classUserMapper.leaveClass(classId, userId) > 0;
    }

    @Override
    public List<ClassUser> getClassUsers(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return classUserMapper.getClassUsers(classId);
    }

    @Override
    @Transactional
    public boolean importStudents(Long classId, List<Long> studentIds) {
        if (classId == null || studentIds == null || studentIds.isEmpty()) {
            throw new IllegalArgumentException("班级ID和学生ID列表不能为空");
        }
        
        // 检查班级是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        int successCount = 0;
        for (Long studentId : studentIds) {
            try {
                if (classUserMapper.isUserInClass(classId, studentId) == 0) {
                    classUserMapper.joinClass(classId, studentId);
                    successCount++;
                }
            } catch (Exception e) {
                // 记录失败的学生ID
                System.err.println("导入学生失败: " + studentId + ", 原因: " + e.getMessage());
            }
        }
        
        return successCount > 0;
    }

    @Override
    @Transactional
    public ImportRecord addStudent(Long classId, Long studentId) {
        if (classId == null || studentId == null) {
            throw new IllegalArgumentException("班级ID和学生ID不能为空");
        }
        
        // 检查班级是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        // 检查用户是否已经在班级中
        if (classUserMapper.isUserInClass(classId, studentId) > 0) {
            throw new IllegalArgumentException("用户已在班级中");
        }
        
        // 添加学生到班级
        classUserMapper.joinClass(classId, studentId);
        
        // 创建导入记录
        ImportRecord record = new ImportRecord();
        record.setClassId(classId);
        record.setOperatorId(studentId); // 这里可以传入操作者ID
        record.setImportTime(LocalDateTime.now());
        record.setImportType("MANUAL_ADD");
        record.setTotalCount(1);
        record.setSuccessCount(1);
        record.setFailCount(0);
        
        return record;
    }

    @Override
    @Transactional
    public void removeStudent(Long classId, Long studentId) {
        if (classId == null || studentId == null) {
            throw new IllegalArgumentException("班级ID和学生ID不能为空");
        }
        
        // 检查用户是否在班级中
        if (classUserMapper.isUserInClass(classId, studentId) == 0) {
            throw new IllegalArgumentException("用户不在班级中");
        }
        
        classUserMapper.leaveClass(classId, studentId);
    }

    @Override
    @Transactional
    public ImportRecord joinClassByCode(String classCode, Long studentId) {
        if (!StringUtils.hasText(classCode) || studentId == null) {
            throw new IllegalArgumentException("班级代码和学生ID不能为空");
        }
        
        // 根据班级代码查找班级
        Class clazz = classUserMapper.getClassByCode(classCode);
        if (clazz == null) {
            throw new IllegalArgumentException("班级代码不存在");
        }
        
        // 检查用户是否已经在班级中
        if (classUserMapper.isUserInClass(clazz.getId(), studentId) > 0) {
            throw new IllegalArgumentException("用户已在班级中");
        }
        
        // 添加学生到班级
        classUserMapper.joinClass(clazz.getId(), studentId);
        
        // 创建导入记录
        ImportRecord record = new ImportRecord();
        record.setClassId(clazz.getId());
        record.setOperatorId(studentId);
        record.setImportTime(LocalDateTime.now());
        record.setImportType("CODE_JOIN");
        record.setTotalCount(1);
        record.setSuccessCount(1);
        record.setFailCount(0);
        
        return record;
    }

    @Override
    public List<ClassDetailDTO> getClassesByUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return classMapper.getClassesByUserId(userId);
    }

    @Override
    public int getClassStudentCount(Long classId) {
        if (classId == null) {
            throw new IllegalArgumentException("班级ID不能为空");
        }
        return classMapper.getClassStudentCount(classId);
    }
    
    // 检查班级代码是否存在
    private boolean isClassCodeExists(String classCode) {
        return classMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Class>()
                .eq("class_code", classCode)
        ) > 0;
    }
}
