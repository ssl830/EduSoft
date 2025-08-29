package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Class;
import org.example.edusoft.dto.ClassDetailDTO;
import org.example.edusoft.entity.ClassUser;
import org.example.edusoft.entity.ImportRecord;
import org.example.edusoft.mapper.ClassMapper;
import org.example.edusoft.mapper.ClassUserMapper;
import org.example.edusoft.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.example.edusoft.client.UserServiceClient;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private ClassUserMapper classUserMapper;

    @Autowired
    private UserServiceClient userServiceClient;

    @Value("${services.user.base-url:http://localhost:8081}")
    private String userServiceBaseUrl;

    private String resolveOutboundToken() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs instanceof ServletRequestAttributes servlet) {
            String satoken = servlet.getRequest().getHeader("satoken");
            if (satoken != null && !satoken.isEmpty())
                return satoken;
            String cookie = servlet.getRequest().getHeader("Cookie");
            if (cookie != null) {
                for (String part : cookie.split(";")) {
                    String p = part.trim();
                    if (p.startsWith("satoken="))
                        return p.substring("satoken=".length());
                }
            }
            String auth = servlet.getRequest().getHeader("Authorization");
            if (auth != null && !auth.isEmpty())
                return auth;
        }
        return null;
    }

    private String fetchUsernameByUserId(Long userId, String token) {
        if (userId == null || token == null) {
            System.out.println("fetchUsernameByUserId: userId=" + userId + ", token=" + token + " - 参数无效");
            return null;
        }
    
        System.out.println("fetchUsernameByUserId: 开始调用用户微服务，userId=" + userId + ", token=" + token);
        System.out.println("fetchUsernameByUserId: 用户微服务地址=" + userServiceBaseUrl);
    
        try {
            // 最外层 Map：{code=200, msg=..., data={...}}
            Map<String, Object> resp = userServiceClient.fetchUserById(
                    userServiceBaseUrl, token, String.valueOf(userId));
            System.out.println("fetchUsernameByUserId: 用户微服务返回数据=" + resp);
    
            if (resp != null) {
                // 取出 data 节点
                Map<String, Object> data = (Map<String, Object>) resp.get("data");
                if (data != null) {
                    Object name = data.get("username");
                    String result = name == null ? null : String.valueOf(name);
                    System.out.println("fetchUsernameByUserId: 提取的用户名=" + result);
                    return result;
                } else {
                    System.out.println("fetchUsernameByUserId: data节点为空");
                }
            } else {
                System.out.println("fetchUsernameByUserId: 用户微服务返回null");
            }
        } catch (Exception e) {
            System.out.println("fetchUsernameByUserId: 调用用户微服务异常=" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

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

        // 插入班级
        classMapper.insert(clazz);

        // 在courseclass表中创建课程班级关联记录
        classMapper.insertCourseClassRelation(clazz.getCourseId(), clazz.getId());

        // 重新查询获取完整的对象
        return classMapper.selectById(clazz.getId());
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
        ClassDetailDTO detail = classMapper.getClassDetailById(id);
        if (detail != null) {
            String token = resolveOutboundToken();
            String teacherName = fetchUsernameByUserId(detail.getTeacherId(), token);
            if (teacherName != null) {
                detail.setTeacherName(teacherName);
            }
        }
        return detail;
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

        // 如果课程ID发生了变化，需要更新courseclass关联表
        if (!existingClass.getCourseId().equals(clazz.getCourseId())) {
            // 删除旧的关联记录
            classMapper.deleteCourseClassRelation(existingClass.getCourseId(), clazz.getId());
            // 创建新的关联记录
            classMapper.insertCourseClassRelation(clazz.getCourseId(), clazz.getId());
        }

        // 更新班级
        classMapper.updateById(clazz);

        // 重新查询获取完整的对象
        return classMapper.selectById(clazz.getId());
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

        // 删除courseclass关联记录（由于外键约束，删除班级时会自动删除关联记录）
        // 但为了明确性，我们也可以手动删除
        classMapper.deleteCourseClassRelationByClassId(id);

        // 删除班级
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
        // 原返回里 studentName 可能为空，这里可按需补充。
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
        record.setOperatorId(studentId);
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
        List<ClassDetailDTO> list = classMapper.getClassesByUserId(userId);
        String token = resolveOutboundToken();
        for (ClassDetailDTO dto : list) {
            String teacherName = fetchUsernameByUserId(dto.getTeacherId(), token);
            if (teacherName != null) {
                dto.setTeacherName(teacherName);
            }
        }
        return list;
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
                        .eq("class_code", classCode)) > 0;
    }
}
