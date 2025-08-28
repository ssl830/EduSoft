package org.example.edusoft.controller.practice;

import org.example.edusoft.common.Result;
import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.PracticeListDTO;
import org.example.edusoft.service.practice.PracticeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PracticeController.class)
class PracticeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PracticeService practiceService;

    // Utility: mock StpUtil static
    void mockLoginTrue(Long id) {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        if (id != null)
            Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                    .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(id);
    }
    void mockLoginFalse() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);
    }

    // createPractice
    @Test
    void createPractice_success() throws Exception {
        Practice practice = new Practice();
        practice.setId(123L);
        Mockito.when(practiceService.createPractice(any())).thenReturn(practice);

        mockMvc.perform(post("/api/practice/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"xxx\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.practiceId").value(123))
                .andExpect(jsonPath("$.msg").value("练习创建成功"));
    }

    @Test
    void createPractice_fail() throws Exception {
        Mockito.when(practiceService.createPractice(any())).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(post("/api/practice/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("创建练习失败：")));
    }

    // updatePractice
    @Test
    void updatePractice_success() throws Exception {
        Practice practice = new Practice();
        practice.setId(1L);
        Mockito.when(practiceService.updatePractice(any())).thenReturn(practice);

        mockMvc.perform(put("/api/practice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"xx\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.msg").value("练习更新成功"));
    }

    @Test
    void updatePractice_fail() throws Exception {
        Mockito.when(practiceService.updatePractice(any())).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(put("/api/practice/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"xx\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("更新练习失败：")));
    }

    // getPracticeList
    @Test
    void getPracticeList_success() throws Exception {
        List<Practice> list = Arrays.asList(new Practice(), new Practice());
        Mockito.when(practiceService.getPracticeList(1L)).thenReturn(list);

        mockMvc.perform(get("/api/practice/list?classId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取练习列表成功"));
    }

    @Test
    void getPracticeList_fail() throws Exception {
        Mockito.when(practiceService.getPracticeList(1L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/list?classId=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取练习列表失败：")));
    }

    // getPracticeDetail
    @Test
    void getPracticeDetail_success() throws Exception {
        Practice practice = new Practice();
        practice.setId(7L);
        Mockito.when(practiceService.getPracticeDetail(7L)).thenReturn(practice);

        mockMvc.perform(get("/api/practice/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.msg").value("获取练习详情成功"));
    }

    @Test
    void getPracticeDetail_fail() throws Exception {
        Mockito.when(practiceService.getPracticeDetail(7L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取练习详情失败：")));
    }

    // deletePractice
    @Test
    void deletePractice_success() throws Exception {
        Mockito.doNothing().when(practiceService).deletePractice(5L);
        mockMvc.perform(delete("/api/practice/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("练习删除成功"));
    }

    @Test
    void deletePractice_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(practiceService).deletePractice(5L);
        mockMvc.perform(delete("/api/practice/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("删除练习失败：")));
    }

    // addQuestionToPractice
    @Test
    void addQuestionToPractice_success() throws Exception {
        Mockito.doNothing().when(practiceService).addQuestionToPractice(2L, 3L, 5);
        mockMvc.perform(post("/api/practice/2/questions/3?score=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("添加题目成功"));
    }

    @Test
    void addQuestionToPractice_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(practiceService).addQuestionToPractice(2L, 3L, 5);
        mockMvc.perform(post("/api/practice/2/questions/3?score=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("添加题目失败：")));
    }

    // removeQuestionFromPractice
    @Test
    void removeQuestionFromPractice_success() throws Exception {
        Mockito.doNothing().when(practiceService).removeQuestionFromPractice(2L, 3L);
        mockMvc.perform(delete("/api/practice/2/questions/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("移除题目成功"));
    }

    @Test
    void removeQuestionFromPractice_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(practiceService).removeQuestionFromPractice(2L, 3L);
        mockMvc.perform(delete("/api/practice/2/questions/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("移除题目失败：")));
    }

    // getPracticeQuestions
    @Test
    void getPracticeQuestions_success() throws Exception {
        List<Question> list = Arrays.asList(new Question(), new Question());
        Mockito.when(practiceService.getPracticeQuestions(9L)).thenReturn(list);

        mockMvc.perform(get("/api/practice/9/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.msg").value("获取练习题目列表成功"));
    }

    @Test
    void getPracticeQuestions_fail() throws Exception {
        Mockito.when(practiceService.getPracticeQuestions(9L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/9/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取练习题目列表失败：")));
    }

    // favoriteQuestion
    @Test
    void favoriteQuestion_success() throws Exception {
        mockLoginTrue(111L);
        Mockito.doNothing().when(practiceService).favoriteQuestion(111L, 99L);
        mockMvc.perform(post("/api/practice/questions/99/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
    @Test
    void favoriteQuestion_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(post("/api/practice/questions/99/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // unfavoriteQuestion
    @Test
    void unfavoriteQuestion_success() throws Exception {
        mockLoginTrue(111L);
        Mockito.doNothing().when(practiceService).unfavoriteQuestion(111L, 99L);
        mockMvc.perform(delete("/api/practice/questions/99/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
    @Test
    void unfavoriteQuestion_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(delete("/api/practice/questions/99/favorite"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getFavoriteQuestions
    @Test
    void getFavoriteQuestions_success() throws Exception {
        mockLoginTrue(111L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>(), new HashMap<>());
        Mockito.when(practiceService.getFavoriteQuestions(111L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/questions/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getFavoriteQuestions_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(get("/api/practice/questions/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // addWrongQuestion
    @Test
    void addWrongQuestion_success() throws Exception {
        mockLoginTrue(111L);
        Mockito.doNothing().when(practiceService).addWrongQuestion(111L, 99L, "abc");
        mockMvc.perform(post("/api/practice/questions/99/wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrongAnswer\":\"abc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
    @Test
    void addWrongQuestion_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(post("/api/practice/questions/99/wrong")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"wrongAnswer\":\"abc\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getWrongQuestions
    @Test
    void getWrongQuestions_success() throws Exception {
        mockLoginTrue(111L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>(), new HashMap<>());
        Mockito.when(practiceService.getWrongQuestions(111L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/questions/wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getWrongQuestions_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(get("/api/practice/questions/wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getWrongQuestionsByCourse
    @Test
    void getWrongQuestionsByCourse_success() throws Exception {
        mockLoginTrue(111L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>(), new HashMap<>());
        Mockito.when(practiceService.getWrongQuestionsByCourse(111L, 88L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/questions/wrong/course/88"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getWrongQuestionsByCourse_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(get("/api/practice/questions/wrong/course/88"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // removeWrongQuestion
    @Test
    void removeWrongQuestion_success() throws Exception {
        mockLoginTrue(111L);
        Mockito.doNothing().when(practiceService).removeWrongQuestion(111L, 99L);
        mockMvc.perform(delete("/api/practice/questions/99/wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }
    @Test
    void removeWrongQuestion_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(delete("/api/practice/questions/99/wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getCoursePractices
    @Test
    void getCoursePractices_success() throws Exception {
        mockLoginTrue(111L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>(), new HashMap<>());
        Mockito.when(practiceService.getCoursePractices(111L, 99L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/course/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getCoursePractices_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(get("/api/practice/course/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getStudentPracticeList
    @Test
    void getStudentPracticeList_success() throws Exception {
        List<PracticeListDTO> list = Arrays.asList(new PracticeListDTO(), new PracticeListDTO());
        Mockito.when(practiceService.getStudentPracticeList(101L, 202L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/student/list?studentId=101&classId=202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getStudentPracticeList_fail_illegalArgument() throws Exception {
        Mockito.when(practiceService.getStudentPracticeList(101L, 202L)).thenThrow(new IllegalArgumentException("参数错误"));
        mockMvc.perform(get("/api/practice/student/list?studentId=101&classId=202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.msg").value("参数错误"));
    }

    @Test
    void getStudentPracticeList_fail_exception() throws Exception {
        Mockito.when(practiceService.getStudentPracticeList(101L, 202L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/student/list?studentId=101&classId=202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取练习列表失败：")));
    }

    // getPracticeListForTeacher
    @Test
    void getPracticeListForTeacher_success() throws Exception {
        List<Practice> list = Arrays.asList(new Practice(), new Practice());
        Mockito.when(practiceService.getPracticeList(2L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/list/teacher?classId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getPracticeListForTeacher_fail() throws Exception {
        Mockito.when(practiceService.getPracticeList(2L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/practice/list/teacher?classId=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // updateQuestionScore
    @Test
    void updateQuestionScore_success() throws Exception {
        Mockito.doNothing().when(practiceService).addQuestionToPractice(1L, 2L, 99);
        mockMvc.perform(put("/api/practice/1/questions/2?score=99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("题目分值更新成功"));
    }

    @Test
    void updateQuestionScore_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(practiceService).addQuestionToPractice(1L, 2L, 99);
        mockMvc.perform(put("/api/practice/1/questions/2?score=99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // getTeacherPractices
    @Test
    void getTeacherPractices_success() throws Exception {
        mockLoginTrue(222L);
        List<Map<String, Object>> list = Arrays.asList(new HashMap<>(), new HashMap<>());
        Mockito.when(practiceService.getTeacherPractices(222L)).thenReturn(list);
        mockMvc.perform(get("/api/practice/teacher/practices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").exists());
    }
    @Test
    void getTeacherPractices_fail_notLogin() throws Exception {
        mockLoginFalse();
        mockMvc.perform(get("/api/practice/teacher/practices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.msg").value("请先登录"));
    }

    // getPracticeStats
    @Test
    void getPracticeStats_success() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("score", 100);
        Mockito.when(practiceService.getSubmissionStats(5L)).thenReturn(stats);
        mockMvc.perform(get("/api/practice/stats/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.score").value(100));
    }

    // updateScoreRate
    @Test
    void updateScoreRate_success() throws Exception {
        Mockito.doNothing().when(practiceService).updateScoreRateAfterDeadline(7L);
        mockMvc.perform(post("/api/practice/update-score-rate/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("OK"))
                .andExpect(jsonPath("$.msg").value("得分率统计并写入成功"));
    }

    @Test
    void updateScoreRate_fail() throws Exception {
        Mockito.doThrow(new RuntimeException("fail!")).when(practiceService).updateScoreRateAfterDeadline(7L);
        mockMvc.perform(post("/api/practice/update-score-rate/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}