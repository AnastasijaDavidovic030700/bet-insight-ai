package com.betinsight.controller;

import com.betinsight.dto.AnomalyReviewRequest;
import com.betinsight.entity.Anomaly;
import com.betinsight.service.AnomalyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/anomalies")
@CrossOrigin(origins = "http://localhost:5173")
public class AnomalyController {

    private final AnomalyService anomalyService;

    public AnomalyController(AnomalyService anomalyService) {
        this.anomalyService = anomalyService;
    }

    @GetMapping
    public List<Anomaly> getAllAnomalies() {
        return anomalyService.getAllAnomalies();
    }

    @GetMapping("/branch/{branchId}")
    public List<Anomaly> getAnomaliesByBranchId(@PathVariable Long branchId) {
        return anomalyService.getAnomaliesByBranchId(branchId);
    }

    @PostMapping("/generate")
    public List<Anomaly> generateAnomalies() {
        return anomalyService.generateAnomalies();
    }

    @PutMapping("/{id}/review")
    public Anomaly updateReview(@PathVariable Long id, @RequestBody AnomalyReviewRequest request) {
        return anomalyService.updateReview(id, request);
    }
}