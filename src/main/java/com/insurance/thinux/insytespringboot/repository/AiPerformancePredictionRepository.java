package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.AiPerformancePrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiPerformancePredictionRepository extends JpaRepository<AiPerformancePrediction, Long> {

    List<AiPerformancePrediction> findByAgentId(Long agentId);

    List<AiPerformancePrediction> findByPredictionYearAndPredictionMonth(Integer predictionYear, Integer predictionMonth);
}
