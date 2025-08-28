package org.example.edusoft.controller.admin;

import org.example.edusoft.service.admin.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.example.edusoft.common.Result;

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
        return Result.success(dashboardService.getDashboardOverview());
    }
} 