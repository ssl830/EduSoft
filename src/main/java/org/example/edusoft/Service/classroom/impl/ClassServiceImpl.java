package org.example.edusoft.service.classroom.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.edusoft.common.exception.BusinessException;
import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.mapper.classroom.ClassMapper;
import org.example.edusoft.mapper.classroom.ClassUserMapper;
import org.example.edusoft.mapper.imports.ImportRecordMapper;
import org.example.edusoft.mapper.course.CourseMapper;
import org.example.edusoft.mapper.course.CourseClassMapper;
import org.example.edusoft.service.classroom.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
//import java.util.Map;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ClassUserMapper classUserMapper;

    @Autowired
    private ImportRecordMapper importRecordMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseClassMapper courseClassMapper;

    @Override
    @Transactional
    public Class createClass(Class clazz) {
        // 基本验证
        if (clazz.getCourseId() == null) {
            throw new BusinessException(400, "课程ID不能为空");
        }
        if (clazz.getClassCode() == null || clazz.getClassCode().trim().isEmpty()) {
            throw new BusinessException(400, "班级代码不能为空");
        }
        if (clazz.getName() == null || clazz.getName().trim().isEmpty()) {
            throw new BusinessException(400, "班级名称不能为空");
        }
        if (clazz.getCreatorId() == null) {
            throw new BusinessException(400, "创建者ID不能为空");
        }

        // 检查课程是否存在
        var course = courseMapper.selectById(clazz.getCourseId());
        if (course == null) {
            throw new BusinessException(404, "课程不存在");
        }

        // 1. 创建班级
        classMapper.insert(clazz);

        // 2. 创建课程班级关联
        courseClassMapper.insertCourseClass(clazz.getCourseId(), clazz.getId());

        // 3. 创建班级用户关联（将创建者添加为班级成员）
        ClassUser creator = new ClassUser();
        creator.setClassId(clazz.getId());
        creator.setUserId(clazz.getCreatorId());
        creator.setJoinedAt(LocalDateTime.now());
        classUserMapper.insert(creator);

        // 4. 如果创建者不是课程教师，也将课程教师添加为班级成员
        if (!course.getTeacherId().equals(clazz.getCreatorId())) {
            ClassUser courseTeacher = new ClassUser();
            courseTeacher.setClassId(clazz.getId());
            courseTeacher.setUserId(course.getTeacherId());
            courseTeacher.setJoinedAt(LocalDateTime.now());
            classUserMapper.insert(courseTeacher);
        }

        return clazz;
    }

    @Override
    public List<Class> getClassesByTeacherId(Long teacherId) {
        if (teacherId == null) {
            throw new BusinessException(400, "教师ID不能为空");
        }
        return classMapper.getClassesByTeacherId(teacherId);
    }

    @Override
    public List<Class> getClassesByStudentId(Long studentId) {
        if (studentId == null) {
            throw new BusinessException(400, "学生ID不能为空");
        }
        return classMapper.getClassesByStudentId(studentId);
    }

    @Override
    public Class getClassById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new BusinessException(404, "班级不存在");
        }
        return clazz;
    }

    @Override
    public ClassDetailDTO getClassDetailById(Long id) {
        if (id == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        
        // 先检查班级是否存在
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new BusinessException(404, "班级不存在");
        }
        
        // 获取班级详细信息
        ClassDetailDTO classDetail = classMapper.getClassDetailById(id);
        if (classDetail == null) {
            throw new BusinessException(500, "获取班级详情失败");
        }
        
        return classDetail;
    }

    @Override
    @Transactional
    public Class updateClass(Class clazz) {
        if (clazz.getId() == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        if (classMapper.selectById(clazz.getId()) == null) {
            throw new BusinessException(404, "班级不存在");
        }
        classMapper.updateById(clazz);
        return clazz;
    }

    @Override
    @Transactional
    public boolean deleteClass(Long id) {
        if (id == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        if (classMapper.selectById(id) == null) {
            throw new BusinessException(404, "班级不存在");
        }
        // 先删除班级成员
        QueryWrapper<ClassUser> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", id);
        classUserMapper.delete(wrapper);
        // 再删除班级
        return classMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public boolean joinClass(Long classId, Long userId) {
        if (classId == null || userId == null) {
            throw new BusinessException(400, "班级ID和学生ID不能为空");
        }
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException(404, "班级不存在");
        }
        // 检查是否已经在班级中
        if (classUserMapper.checkUserInClass(classId, userId) > 0) {
            throw new BusinessException(400, "学生已经在班级中");
        }
        
        ClassUser classUser = new ClassUser();
        classUser.setClassId(classId);
        classUser.setUserId(userId);
        classUser.setJoinedAt(LocalDateTime.now());
        
        return classUserMapper.insert(classUser) > 0;
    }

    @Override
    @Transactional
    public boolean leaveClass(Long classId, Long userId) {
        if (classId == null || userId == null) {
            throw new BusinessException(400, "班级ID和学生ID不能为空");
        }
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException(404, "班级不存在");
        }
        if (classUserMapper.checkUserInClass(classId, userId) == 0) {
            throw new BusinessException(400, "学生不在班级中");
        }
        
        QueryWrapper<ClassUser> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId)
               .eq("user_id", userId);
        return classUserMapper.delete(wrapper) > 0;
    }

    @Override
    public List<ClassUser> getClassUsers(Long classId) {
        if (classId == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException(404, "班级不存在");
        }
        return classUserMapper.getClassUsersByClassId(classId);
    }

    @Override
    @Transactional
    public boolean importStudents(Long classId, List<Long> studentIds) {
        if (classId == null) {
            throw new BusinessException(400, "班级ID不能为空");
        }
        if (studentIds == null || studentIds.isEmpty()) {
            throw new BusinessException(400, "学生ID列表不能为空");
        }
        if (classMapper.selectById(classId) == null) {
            throw new BusinessException(404, "班级不存在");
        }

        for (Long studentId : studentIds) {
            if (classUserMapper.checkUserInClass(classId, studentId) == 0) {
                ClassUser classUser = new ClassUser();
                classUser.setClassId(classId);
                classUser.setUserId(studentId);
                classUser.setJoinedAt(LocalDateTime.now());
                classUserMapper.insert(classUser);
            }
        }
        return true;
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
        
        // 检查学生是否已在班级中
        if (classUserMapper.checkUserInClass(classId, studentId) > 0) {
            throw new IllegalArgumentException("学生已在班级中");
        }

        // 添加学生到班级
        ClassUser classUser = new ClassUser();
        classUser.setClassId(classId);
        classUser.setUserId(studentId);
        classUser.setJoinedAt(LocalDateTime.now());
        classUserMapper.insert(classUser);

        // 创建导入记录
        ImportRecord record = new ImportRecord();
        record.setClassId(classId);
        record.setOperatorId(studentId);
        record.setImportTime(LocalDateTime.now());
        record.setImportType("MANUAL_ADD");
        record.setTotalCount(1);
        record.setSuccessCount(1);
        record.setFailCount(0);
        importRecordMapper.insert(record);

        return record;
    }

    @Override
    @Transactional
    public void removeStudent(Long classId, Long studentId) {
        if (classId == null || studentId == null) {
            throw new IllegalArgumentException("班级ID和学生ID不能为空");
        }
        
        // 检查班级是否存在
        Class clazz = classMapper.selectById(classId);
        if (clazz == null) {
            throw new IllegalArgumentException("班级不存在");
        }
        
        // 检查学生是否在班级中
        if (classUserMapper.checkUserInClass(classId, studentId) == 0) {
            throw new IllegalArgumentException("学生不在班级中");
        }

        // 从班级中删除学生
        QueryWrapper<ClassUser> wrapper = new QueryWrapper<>();
        wrapper.eq("class_id", classId)
               .eq("user_id", studentId);
        classUserMapper.delete(wrapper);
    }

    @Override
    @Transactional
    public ImportRecord joinClassByCode(String classCode, Long studentId) {
        if (!StringUtils.hasText(classCode) || studentId == null) {
            throw new IllegalArgumentException("班级代码和学生ID不能为空");
        }
        
        // 通过班级代码查找班级
        QueryWrapper<Class> wrapper = new QueryWrapper<>();
        wrapper.eq("class_code", classCode);
        Class clazz = classMapper.selectOne(wrapper);
        
        if (clazz == null) {
            throw new IllegalArgumentException("班级代码无效");
        }

        // 检查学生是否已在班级中
        if (classUserMapper.checkUserInClass(clazz.getId(), studentId) > 0) {
            throw new IllegalArgumentException("学生已在班级中");
        }

        // 添加学生到班级
        ClassUser classUser = new ClassUser();
        classUser.setClassId(clazz.getId());
        classUser.setUserId(studentId);
        classUser.setJoinedAt(LocalDateTime.now());
        classUserMapper.insert(classUser);

        // 创建导入记录
        ImportRecord record = new ImportRecord();
        record.setClassId(clazz.getId());
        record.setOperatorId(studentId);
        record.setImportTime(LocalDateTime.now());
        record.setImportType("CODE_JOIN");
        record.setTotalCount(1);
        record.setSuccessCount(1);
        record.setFailCount(0);
        importRecordMapper.insert(record);

        return record;
    }
    
    // 检查班级代码是否存在
    private boolean isClassCodeExists(String classCode) {
        QueryWrapper<Class> wrapper = new QueryWrapper<>();
        wrapper.eq("class_code", classCode);
        return classMapper.selectCount(wrapper) > 0;
    }

    /*@Override
    public List<Class> getClassesByUserId(Long userId) {
        return classMapper.getClassesByUserId(userId);
    }*/

    @Override
    public List<ClassDetailDTO> getClassesByUserId(Long userId) {
        if (userId == null) {
            throw new BusinessException(400, "用户ID不能为空");
        }
        List<ClassDetailDTO> classes = classMapper.getClassesByUserId(userId);
        if (classes == null || classes.isEmpty()) {
            throw new BusinessException(404, "未找到相关班级信息");
        }
        return classes;
    }

    @Override
    public int getClassStudentCount(Long classId) {
        return classMapper.getClassStudentCount(classId);
    }
}
