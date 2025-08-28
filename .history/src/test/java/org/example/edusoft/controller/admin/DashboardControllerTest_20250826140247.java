package org.example.edusoft.controller.admin;

import org.example.edusoft.common.Result;
import org.example.edusoft.service.admin.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DashboardService dashboardService;

    private Map<String, Object> overviewData;

    @BeforeEach
    void setUp() {
        overviewData = new HashMap<>();
        overviewData.put("todayTeacherCount", 5);
        overviewData.put("todayStudentCount", 100);
        overviewData.put("weekTeacherCount", 12);
        overviewData.put("weekStudentCount", 350);
    }

    // 正向用例：正常返回统计数据
    @Test
    void getOverview_success() throws Exception {
        when(dashboardService.getDashboardOverview()).thenReturn(overviewData);

        mockMvc.perform(get("/api/admin/dashboard/overview")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.todayTeacherCount").value(5))
                .andExpect(jsonPath("$.data.weekStudentCount").value(350));
    }

    // 反向用例：service抛异常，controller应该返回错误
    @Test
    void getOverview_serviceThrowsException() throws Exception {
        when(dashboardService.getDashboardOverview()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/admin/dashboard/overview")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500)) // 假设Result.error返回500
                .andExpect(jsonPath("$.msg").exists());
    }
}