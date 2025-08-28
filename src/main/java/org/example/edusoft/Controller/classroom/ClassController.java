package org.example.edusoft.controller.classroom;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.service.classroom.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.example.edusoft.entity.classroom.TeacherClassDTO;
import org.example.edusoft.service.classroom.TeacherClassService;

@RestController
@RequestMapping("/api/classes")
@Validated
public class ClassController {

    @Autowired
    private ClassService classService;

    @Autowired
    private TeacherClassService teacherClassService;

    @PostMapping
    public Result<Class> createClass(@Valid @RequestBody Class clazz) {
        try {
            return Result.success(classService.createClass(clazz));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
//这两个接口不用
    @GetMapping("/teacher/{teacherId}")
    public Result<List<Class>> getClassesByTeacherId(@NotNull(message = "教师ID不能为空") @PathVariable Long teacherId) {
        try {
            return Result.success(classService.getClassesByTeacherId(teacherId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public Result<List<Class>> getClassesByStudentId(@NotNull(message = "学生ID不能为空") @PathVariable Long studentId) {
        try {
            return Result.success(classService.getClassesByStudentId(studentId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
//上面两个接口不用

//获取班级详情
    @GetMapping("/{id}")
    public Result<ClassDetailDTO> getClassById(@PathVariable Long id) {
        return Result.success(classService.getClassDetailById(id));
    }

    @PutMapping("/{id}")
    public Result<Class> updateClass(@NotNull(message = "班级ID不能为空") @PathVariable Long id, 
                                   @Valid @RequestBody Class clazz) {
        try {
            clazz.setId(id);
            return Result.success(classService.updateClass(clazz));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteClass(@NotNull(message = "班级ID不能为空") @PathVariable Long id) {
        try {
            return Result.success(classService.deleteClass(id));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{classId}/join/{userId}")
    public Result<Boolean> joinClass(@NotNull(message = "班级ID不能为空") @PathVariable Long classId,
                                   @NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        try {
            return Result.success(classService.joinClass(classId, userId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{classId}/leave/{userId}")
    public Result<Boolean> leaveClass(@NotNull(message = "班级ID不能为空") @PathVariable Long classId,
                                    @NotNull(message = "用户ID不能为空") @PathVariable Long userId) {
        try {
            return Result.success(classService.leaveClass(classId, userId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{classId}/users")
    public Result<List<ClassUser>> getClassUsers(@NotNull(message = "班级ID不能为空") @PathVariable Long classId) {
        try {
            return Result.success(classService.getClassUsers(classId));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{classId}/import")
    public Result<Boolean> importStudents(@NotNull(message = "班级ID不能为空") @PathVariable Long classId,
                                        @RequestBody List<Long> studentIds) {
        try {
            return Result.success(classService.importStudents(classId, studentIds));
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{classId}/students")
    public Result<ImportRecord> addStudent(@NotNull(message = "班级ID不能为空") @PathVariable Long classId,
                                         @NotNull(message = "学生ID不能为空") @RequestParam Long studentId) {
        try {
            ImportRecord record = classService.addStudent(classId, studentId);
            return Result.success(record);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{classId}/students/{studentId}")
    public Result<Void> removeStudent(@NotNull(message = "班级ID不能为空") @PathVariable Long classId,
                                    @NotNull(message = "学生ID不能为空") @PathVariable Long studentId) {
        try {
            classService.removeStudent(classId, studentId);
            return Result.success(null);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/join")
    public Result<ImportRecord> joinClassByCode(@NotBlank(message = "班级代码不能为空") @RequestParam String classCode,
                                              @NotNull(message = "学生ID不能为空") @RequestParam Long studentId) {
        try {
            ImportRecord record = classService.joinClassByCode(classCode, studentId);
            return Result.success(record);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }
//使用统一接口，根据用户id获取用户的班级列表，返回id、course_id、course_name、teacherID、class_name、class_code
//如果是老师返回老师教的班级列表，如果是学生返回学生所在的班级列表
//你可以更改classDetail类（在保证与上面的接口兼容的情况下）
    @GetMapping("/user/{userId}")
    public Result<List<ClassDetailDTO>> getClassesByUserId(@PathVariable Long userId) {
        return Result.success(classService.getClassesByUserId(userId));
    }

    @GetMapping("/teacher/simple/{teacherId}")
    public Result<List<TeacherClassDTO>> getSimpleClassesByTeacherId(@PathVariable Long teacherId) {
        return Result.success(teacherClassService.getClassesByTeacherId(teacherId));
    }

    // 课表API，返回用户所有班级信息
    @GetMapping("/schedule/user/{userId}")
    public Result<List<ClassDetailDTO>> getScheduleByUserId(@PathVariable Long userId) {
        return Result.success(classService.getClassesByUserId(userId));
    }

    @GetMapping("/{classId}/student-count")
    public Result<Object> getClassStudentCount(@PathVariable Long classId) {
        int total = classService.getClassStudentCount(classId);
        return Result.success(Map.of("total", total));
    }
}