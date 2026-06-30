package com.betinsight.repository;

import com.betinsight.entity.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyReportRepository extends JpaRepository<DailyReport, Long> {

    List<DailyReport> findByBranchIdOrderByReportDateAsc(Long branchId);

    List<DailyReport> findByReportDateBetweenOrderByReportDateAsc(LocalDate periodFrom, LocalDate periodTo);
}