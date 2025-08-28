package org.example.edusoft.service.classroom;

import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import java.util.List;

public interface ClassService {
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
}