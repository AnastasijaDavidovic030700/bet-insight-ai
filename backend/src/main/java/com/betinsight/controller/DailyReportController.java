package com.betinsight.controller;

import com.betinsight.dto.DailyReportRequest;
import com.betinsight.entity.DailyReport;
import com.betinsight.service.DailyReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    @GetMapping
    public List<DailyReport> getAllReports() {
        return dailyReportService.getAllReports();
    }

    @GetMapping("/{id}")
    public DailyReport getReportById(@PathVariable Long id) {
        return dailyReportService.getReportById(id);
    }

    @GetMapping("/branch/{branchId}")
    public List<DailyReport> getReportsByBranchId(@PathVariable Long branchId) {
        return dailyReportService.getReportsByBranchId(branchId);
    }

    @PostMapping
    public DailyReport createReport(@RequestBody DailyReportRequest request) {
        return dailyReportService.createReport(request);
    }

    @PutMapping("/{id}")
    public DailyReport updateReport(@PathVariable Long id, @RequestBody DailyReportRequest request) {
        return dailyReportService.updateReport(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteReport(@PathVariable Long id) {
        dailyReportService.deleteReport(id);
    }
}