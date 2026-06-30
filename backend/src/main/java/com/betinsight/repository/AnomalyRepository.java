package com.betinsight.repository;

import com.betinsight.entity.Anomaly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnomalyRepository extends JpaRepository<Anomaly, Long> {

    List<Anomaly> findAllByOrderByCreatedAtDesc();

    List<Anomaly> findByBranchIdOrderByCreatedAtDesc(Long branchId);

    boolean existsByDailyReportIdAndType(Long dailyReportId, String type);

    void deleteByDailyReportId(Long dailyReportId);
}