package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.response.RecommendationResponseDTO;

import java.util.List;

public interface RecommendationService {

    List<RecommendationResponseDTO> getAllRecommendations();

    List<RecommendationResponseDTO> getRecommendationsByAgent(Long agentId);

    List<RecommendationResponseDTO> getRecommendationsBySupervisor(Long supervisorId);

    RecommendationResponseDTO markRecommendationAsViewed(Long id);
}
