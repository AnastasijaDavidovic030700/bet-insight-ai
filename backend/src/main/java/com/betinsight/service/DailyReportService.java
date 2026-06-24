package com.betinsight.service;

import com.betinsight.dto.DailyReportRequest;
import com.betinsight.entity.Branch;
import com.betinsight.entity.DailyReport;
import com.betinsight.repository.DailyReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DailyReportService {

    private final DailyReportRepository dailyReportRepository;
    private final BranchService branchService;

    public DailyReportService(DailyReportRepository dailyReportRepository, BranchService branchService) {
        this.dailyReportRepository = dailyReportRepository;
        this.branchService = branchService;
    }

    public List<DailyReport> getAllReports() {
        return dailyReportRepository.findAll();
    }

    public DailyReport getReportById(Long id) {
        return dailyReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Daily report not found with id: " + id));
    }

    public List<DailyReport> getReportsByBranchId(Long branchId) {
        return dailyReportRepository.findByBranchIdOrderByReportDateAsc(branchId);
    }

    public DailyReport createReport(DailyReportRequest request) {
        Branch branch = branchService.getBranchById(request.getBranchId());

        DailyReport report = new DailyReport();

        report.setBranch(branch);
        report.setReportDate(request.getReportDate());
        report.setNumberOfTickets(request.getNumberOfTickets());
        report.setTotalPayments(request.getTotalPayments());
        report.setTotalPayouts(request.getTotalPayouts());
        report.setNote(request.getNote());

        calculateBusinessFields(report);

        return dailyReportRepository.save(report);
    }

    public DailyReport updateReport(Long id, DailyReportRequest request) {
        DailyReport existingReport = getReportById(id);
        Branch branch = branchService.getBranchById(request.getBranchId());

        existingReport.setBranch(branch);
        existingReport.setReportDate(request.getReportDate());
        existingReport.setNumberOfTickets(request.getNumberOfTickets());
        existingReport.setTotalPayments(request.getTotalPayments());
        existingReport.setTotalPayouts(request.getTotalPayouts());
        existingReport.setNote(request.getNote());

        calculateBusinessFields(existingReport);

        return dailyReportRepository.save(existingReport);
    }

    public void deleteReport(Long id) {
        DailyReport existingReport = getReportById(id);
        dailyReportRepository.delete(existingReport);
    }

    private void calculateBusinessFields(DailyReport report) {
        BigDecimal grossProfit = report.getTotalPayments().subtract(report.getTotalPayouts());
        report.setGrossProfit(grossProfit);

        if (report.getNumberOfTickets() != null && report.getNumberOfTickets() > 0) {
            BigDecimal averageTicketAmount = report.getTotalPayments()
                    .divide(BigDecimal.valueOf(report.getNumberOfTickets()), 2, RoundingMode.HALF_UP);

            report.setAverageTicketAmount(averageTicketAmount);
        } else {
            report.setAverageTicketAmount(BigDecimal.ZERO);
        }
    }
}