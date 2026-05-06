package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.RecommendationResponseDTO;
import com.insurance.thinux.insytespringboot.enums.RecommendationStatus;
import com.insurance.thinux.insytespringboot.mapper.RecommendationMapper;
import com.insurance.thinux.insytespringboot.model.AiRecommendation;
import com.insurance.thinux.insytespringboot.repository.AiRecommendationRepository;
import com.insurance.thinux.insytespringboot.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final AiRecommendationRepository aiRecommendationRepository;

    @Override
    public List<RecommendationResponseDTO> getAllRecommendations() {
        return aiRecommendationRepository.findAll().stream().map(RecommendationMapper::toResponseDTO).toList();
    }

    @Override
    public List<RecommendationResponseDTO> getRecommendationsByAgent(Long agentId) {
        return aiRecommendationRepository.findByAgentId(agentId).stream().map(RecommendationMapper::toResponseDTO).toList();
    }

    @Override
    public List<RecommendationResponseDTO> getRecommendationsBySupervisor(Long supervisorId) {
        return aiRecommendationRepository.findBySupervisorId(supervisorId).stream().map(RecommendationMapper::toResponseDTO).toList();
    }

    @Override
    public RecommendationResponseDTO markRecommendationAsViewed(Long id) {

        AiRecommendation recommendation = aiRecommendationRepository.findById(id).orElseThrow(() -> new RuntimeException("Recommendation not found with id: " + id));

        recommendation.setStatus(RecommendationStatus.VIEWED);
        recommendation.setViewedAt(LocalDateTime.now());

        AiRecommendation updatedRecommendation = aiRecommendationRepository.save(recommendation);

        return RecommendationMapper.toResponseDTO(updatedRecommendation);
    }
}
