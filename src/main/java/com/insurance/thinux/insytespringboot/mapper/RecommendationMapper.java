package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.RecommendationResponseDTO;
import com.insurance.thinux.insytespringboot.model.AiRecommendation;
import com.insurance.thinux.insytespringboot.model.User;

public class RecommendationMapper {
    private RecommendationMapper() {
        // Prevent object creation
    }

    public static RecommendationResponseDTO toResponseDTO(AiRecommendation recommendation) {

        if (recommendation == null) {
            return null;
        }

        User agent = recommendation.getAgent();
        User supervisor = recommendation.getSupervisor();

        RecommendationResponseDTO dto = new RecommendationResponseDTO();

        dto.setId(recommendation.getId());

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

        dto.setRecommendationType(recommendation.getRecommendationType());
        dto.setTitle(recommendation.getTitle());
        dto.setRecommendationText(recommendation.getRecommendationText());

        dto.setPriority(recommendation.getPriority());
        dto.setStatus(recommendation.getStatus());

        dto.setGeneratedAt(recommendation.getGeneratedAt());
        dto.setViewedAt(recommendation.getViewedAt());

        return dto;
    }
}
