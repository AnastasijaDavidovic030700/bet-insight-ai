package com.betinsight.controller;

import com.betinsight.dto.AiInsightRequest;
import com.betinsight.entity.AiInsight;
import com.betinsight.service.AiInsightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-insights")
@CrossOrigin(origins = "http://localhost:5173")
public class AiInsightController {

    private final AiInsightService aiInsightService;

    public AiInsightController(AiInsightService aiInsightService) {
        this.aiInsightService = aiInsightService;
    }

    @GetMapping
    public List<AiInsight> getAllInsights() {
        return aiInsightService.getAllInsights();
    }

    @PostMapping("/generate")
    public AiInsight generateInsight(@RequestBody AiInsightRequest request) {
        return aiInsightService.generateInsight(request);
    }
}