package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.AiPredictionResponseDTO;
import com.insurance.thinux.insytespringboot.model.AiPerformancePrediction;
import com.insurance.thinux.insytespringboot.model.User;

public class AiPredictionMapper {

    private AiPredictionMapper() {
        // Prevent object creation
    }

    public static AiPredictionResponseDTO toResponseDTO(AiPerformancePrediction prediction) {

        if (prediction == null) {
            return null;
        }

        User agent = prediction.getAgent();
        User supervisor = agent != null ? agent.getSupervisor() : null;

        AiPredictionResponseDTO dto = new AiPredictionResponseDTO();

        dto.setId(prediction.getId());

        if (agent != null) {
            dto.setAgentId(agent.getId());
            dto.setUsername(agent.getUsername());
            dto.setNickname(agent.getNickname());
            dto.setEmail(agent.getEmail());
            dto.setPhone(agent.getPhone());
        }

        if (supervisor != null) {
            dto.setSupervisorId(supervisor.getId());
            dto.setSupervisorUsername(supervisor.getUsername());
        }

        dto.setPredictionYear(prediction.getPredictionYear());
        dto.setPredictionMonth(prediction.getPredictionMonth());

        dto.setPredictedTotalPremium(prediction.getPredictedTotalPremium());
        dto.setPredictedConversionRate(prediction.getPredictedConversionRate());
        dto.setPredictedPerformanceScore(prediction.getPredictedPerformanceScore());

        dto.setRiskLevel(prediction.getRiskLevel());
        dto.setPredictionLabel(prediction.getPredictionLabel());

        dto.setModelName(prediction.getModelName());
        dto.setModelVersion(prediction.getModelVersion());
        dto.setConfidenceScore(prediction.getConfidenceScore());

        dto.setGeneratedAt(prediction.getGeneratedAt());

        return dto;
    }
}
