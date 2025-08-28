package org.example.edusoft.controller.classroom;

import org.example.edusoft.entity.classroom.Class;
import org.example.edusoft.entity.classroom.ClassUser;
import org.example.edusoft.entity.classroom.ClassDetailDTO;
import org.example.edusoft.entity.classroom.TeacherClassDTO;
import org.example.edusoft.entity.imports.ImportRecord;
import org.example.edusoft.service.classroom.ClassService;
import org.example.edusoft.service.classroom.TeacherClassService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClassController.class)
class ClassControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassService classService;

    @MockBean
    private TeacherClassService teacherClassService;

    // 1. createClass
    @Test
    void createClass_success() throws Exception {
        Class mockClass = new Class();
        mockClass.setId(1L);
        mockClass.setName("Test Class");
        
        Mockito.when(classService.createClass(any(Class.class))).thenReturn(mockClass);

        mockMvc.perform(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"className\":\"Test Class\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.className").value("Test Class"));
    }

    @Test
    void createClass_fail() throws Exception {
        Mockito.when(classService.createClass(any(Class.class)))
                .thenThrow(new IllegalArgumentException("班级名称不能为空"));

        mockMvc.perform(post("/api/classes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"className\":\"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("班级名称不能为空"));
    }

    // 2. getClassById
    @Test
    void getClassById_success() throws Exception {
        ClassDetailDTO mockDetail = new ClassDetailDTO();
        mockDetail.setId(1L);
        mockDetail.setClassName("Test Class");
        
        Mockito.when(classService.getClassDetailById(1L)).thenReturn(mockDetail);

        mockMvc.perform(get("/api/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.className").value("Test Class"));
    }

    @Test
    void getClassById_fail() throws Exception {
        Mockito.when(classService.getClassDetailById(1L))
                .thenThrow(new RuntimeException("班级不存在"));

        mockMvc.perform(get("/api/classes/1"))
                .andExpect(status().is5xxServerError());
    }

    // 3. updateClass
    @Test
    void updateClass_success() throws Exception {
        Class mockClass = new Class();
        mockClass.setId(1L);
        mockClass.setName("Updated Class");
        
        Mockito.when(classService.updateClass(any(Class.class))).thenReturn(mockClass);

        mockMvc.perform(put("/api/classes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"className\":\"Updated Class\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.className").value("Updated Class"));
    }

    @Test
    void updateClass_fail() throws Exception {
        Mockito.when(classService.updateClass(any(Class.class)))
                .thenThrow(new IllegalArgumentException("更新失败"));

        mockMvc.perform(put("/api/classes/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"className\":\"Updated Class\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("更新失败"));
    }

    // 4. deleteClass
    @Test
    void deleteClass_success() throws Exception {
        Mockito.when(classService.deleteClass(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/classes/1"))
                .andExpected(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteClass_fail() throws Exception {
        Mockito.when(classService.deleteClass(1L))
                .thenThrow(new IllegalArgumentException("删除失败"));

        mockMvc.perform(delete("/api/classes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("删除失败"));
    }

    // 5. joinClass
    @Test
    void joinClass_success() throws Exception {
        Mockito.when(classService.joinClass(1L, 2L)).thenReturn(true);

        mockMvc.perform(post("/api/classes/1/join/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void joinClass_fail() throws Exception {
        Mockito.when(classService.joinClass(1L, 2L))
                .thenThrow(new IllegalArgumentException("加入班级失败"));

        mockMvc.perform(post("/api/classes/1/join/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("加入班级失败"));
    }

    // 6. leaveClass
    @Test
    void leaveClass_success() throws Exception {
        Mockito.when(classService.leaveClass(1L, 2L)).thenReturn(true);

        mockMvc.perform(delete("/api/classes/1/leave/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void leaveClass_fail() throws Exception {
        Mockito.when(classService.leaveClass(1L, 2L))
                .thenThrow(new IllegalArgumentException("离开班级失败"));

        mockMvc.perform(delete("/api/classes/1/leave/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("离开班级失败"));
    }

    // 7. getClassUsers
    @Test
    void getClassUsers_success() throws Exception {
        List<ClassUser> users = Arrays.asList(
            new ClassUser(), new ClassUser()
        );
        Mockito.when(classService.getClassUsers(1L)).thenReturn(users);

        mockMvc.perform(get("/api/classes/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getClassUsers_fail() throws Exception {
        Mockito.when(classService.getClassUsers(1L))
                .thenThrow(new IllegalArgumentException("获取用户列表失败"));

        mockMvc.perform(get("/api/classes/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("获取用户列表失败"));
    }

    // 8. importStudents
    @Test
    void importStudents_success() throws Exception {
        Mockito.when(classService.importStudents(eq(1L), anyList())).thenReturn(true);

        mockMvc.perform(post("/api/classes/1/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void importStudents_fail() throws Exception {
        Mockito.when(classService.importStudents(eq(1L), anyList()))
                .thenThrow(new IllegalArgumentException("导入学生失败"));

        mockMvc.perform(post("/api/classes/1/import")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1,2,3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("导入学生失败"));
    }

    // 9. addStudent
    @Test
    void addStudent_success() throws Exception {
        ImportRecord record = new ImportRecord();
        record.setId(1L);
        Mockito.when(classService.addStudent(1L, 2L)).thenReturn(record);

        mockMvc.perform(post("/api/classes/1/students?studentId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void addStudent_fail() throws Exception {
        Mockito.when(classService.addStudent(1L, 2L))
                .thenThrow(new IllegalArgumentException("添加学生失败"));

        mockMvc.perform(post("/api/classes/1/students?studentId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("添加学生失败"));
    }

    // 10. removeStudent
    @Test
    void removeStudent_success() throws Exception {
        Mockito.doNothing().when(classService).removeStudent(1L, 2L);

        mockMvc.perform(delete("/api/classes/1/students/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void removeStudent_fail() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("移除学生失败"))
                .when(classService).removeStudent(1L, 2L);

        mockMvc.perform(delete("/api/classes/1/students/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("移除学生失败"));
    }

    // 11. joinClassByCode
    @Test
    void joinClassByCode_success() throws Exception {
        ImportRecord record = new ImportRecord();
        record.setId(1L);
        Mockito.when(classService.joinClassByCode("ABC123", 2L)).thenReturn(record);

        mockMvc.perform(post("/api/classes/join?classCode=ABC123&studentId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void joinClassByCode_fail() throws Exception {
        Mockito.when(classService.joinClassByCode("ABC123", 2L))
                .thenThrow(new IllegalArgumentException("班级代码无效"));

        mockMvc.perform(post("/api/classes/join?classCode=ABC123&studentId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("班级代码无效"));
    }

    // 12. getClassesByUserId
    @Test
    void getClassesByUserId_success() throws Exception {
        List<ClassDetailDTO> classes = Arrays.asList(
            new ClassDetailDTO(), new ClassDetailDTO()
        );
        Mockito.when(classService.getClassesByUserId(1L)).thenReturn(classes);

        mockMvc.perform(get("/api/classes/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getClassesByUserId_fail() throws Exception {
        Mockito.when(classService.getClassesByUserId(1L))
                .thenThrow(new RuntimeException("获取班级列表失败"));

        mockMvc.perform(get("/api/classes/user/1"))
                .andExpect(status().is5xxServerError());
    }

    // 13. getSimpleClassesByTeacherId
    @Test
    void getSimpleClassesByTeacherId_success() throws Exception {
        List<TeacherClassDTO> classes = Arrays.asList(
            new TeacherClassDTO(), new TeacherClassDTO()
        );
        Mockito.when(teacherClassService.getClassesByTeacherId(1L)).thenReturn(classes);

        mockMvc.perform(get("/api/classes/teacher/simple/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getSimpleClassesByTeacherId_fail() throws Exception {
        Mockito.when(teacherClassService.getClassesByTeacherId(1L))
                .thenThrow(new RuntimeException("获取教师班级失败"));

        mockMvc.perform(get("/api/classes/teacher/simple/1"))
                .andExpect(status().is5xxServerError());
    }

    // 14. getScheduleByUserId
    @Test
    void getScheduleByUserId_success() throws Exception {
        List<ClassDetailDTO> classes = Arrays.asList(
            new ClassDetailDTO(), new ClassDetailDTO()
        );
        Mockito.when(classService.getClassesByUserId(1L)).thenReturn(classes);

        mockMvc.perform(get("/api/classes/schedule/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void getScheduleByUserId_fail() throws Exception {
        Mockito.when(classService.getClassesByUserId(1L))
                .thenThrow(new RuntimeException("获取课表失败"));

        mockMvc.perform(get("/api/classes/schedule/user/1"))
                .andExpect(status().is5xxServerError());
    }

    // 15. getClassStudentCount
    @Test
    void getClassStudentCount_success() throws Exception {
        Mockito.when(classService.getClassStudentCount(1L)).thenReturn(25);

        mockMvc.perform(get("/api/classes/1/student-count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(25));
    }

    @Test
    void getClassStudentCount_fail() throws Exception {
        Mockito.when(classService.getClassStudentCount(1L))
                .thenThrow(new RuntimeException("获取学生数量失败"));

        mockMvc.perform(get("/api/classes/1/student-count"))
                .andExpect(status().is5xxServerError());
    }
}