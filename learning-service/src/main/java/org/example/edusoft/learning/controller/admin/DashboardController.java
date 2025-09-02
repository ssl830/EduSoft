package org.example.edusoft.learning.controller.admin;

import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.service.admin.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin Dashboard Controller
 *
 * 提供管理端大屏概览所需的数据接口。
 */
@RestController
@RequestMapping("/api/admin/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 概览数据接口
     * @return today / week 的教师 & 学生统计信息
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverview() {
        System.out.println("Fetching dashboard overview data");
        return Result.success(dashboardService.getDashboardOverview());
    }
} 