package com.betinsight.controller;

import com.betinsight.dto.DashboardStatsResponse;
import com.betinsight.service.AnalyticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:5173")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    public DashboardStatsResponse getDashboardStats() {
        return analyticsService.getDashboardStats();
    }
}