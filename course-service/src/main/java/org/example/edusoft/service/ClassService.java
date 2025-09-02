package org.example.edusoft.service;

import org.example.edusoft.entity.Class;
import org.example.edusoft.dto.ClassDetailDTO;
import org.example.edusoft.entity.ClassUser;

import org.example.edusoft.entity.ImportRecord;
import java.util.List;

public interface ClassService {
    /**
     * 查询用户在指定课程下的所有班级（返回完整Class实体）
     */
    List<Class> getClassByUserIdAndCourseId(Long userId, Long courseId);
    /**
     * 根据用户ID和课程ID列表，获取用户在每个课程下的班级名（如有多个班级用逗号拼接），返回JSON字符串
     */
    String getClassesByUserIdAndCourseIds(Long userId, List<Long> courseIds);
    // 创建班级
    Class createClass(Class clazz);
    
    // 获取教师的班级列表
    List<Class> getClassesByTeacherId(Long teacherId);
    
    // 获取学生的班级列表
    List<Class> getClassesByStudentId(Long studentId);
    
    // 获取班级基本信息
    Class getClassById(Long id);
    
    // 获取班级详细信息（包含课程信息）
    ClassDetailDTO getClassDetailById(Long id);
    
    // 更新班级信息
    Class updateClass(Class clazz);
    
    // 删除班级
    boolean deleteClass(Long id);
    
    // 学生加入班级
    boolean joinClass(Long classId, Long userId);
    
    // 学生退出班级
    boolean leaveClass(Long classId, Long userId);
    
    // 获取班级成员列表
    List<ClassUser> getClassUsers(Long classId);
    
    // 批量导入学生
    boolean importStudents(Long classId, List<Long> studentIds);

    /**
     * 手动添加单个学生到班级
     * @param classId 班级ID
     * @param studentId 学生ID
     * @return 导入记录
     */
    ImportRecord addStudent(Long classId, Long studentId);

    /**
     * 从班级中删除学生
     * @param classId 班级ID
     * @param studentId 学生ID
     */
    void removeStudent(Long classId, Long studentId);

    /**
     * 通过班级代码加入班级
     * @param classCode 班级代码
     * @param studentId 学生ID
     * @return 导入记录
     */
    ImportRecord joinClassByCode(String classCode, Long studentId);

    // 获取用户的班级列表（包括教师和学生的班级）
    List<ClassDetailDTO> getClassesByUserId(Long userId);

    // 获取班级学生总数
    int getClassStudentCount(Long classId);

    /**
     * 获取所有班级列表（只返回id和name）
     */
    List<java.util.Map<String, Object>> getAllClasses();
}
