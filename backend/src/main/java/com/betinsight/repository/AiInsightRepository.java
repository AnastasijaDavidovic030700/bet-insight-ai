package com.betinsight.repository;

import com.betinsight.entity.AiInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiInsightRepository extends JpaRepository<AiInsight, Long> {

    List<AiInsight> findAllByOrderByCreatedAtDesc();
}