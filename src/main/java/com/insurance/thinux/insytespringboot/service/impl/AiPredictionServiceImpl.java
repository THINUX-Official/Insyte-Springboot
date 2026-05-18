package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.AiPredictionResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.AiPredictionMapper;
import com.insurance.thinux.insytespringboot.repository.AiPerformancePredictionRepository;
import com.insurance.thinux.insytespringboot.service.AiPredictionService;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiPredictionServiceImpl implements AiPredictionService {

    private final AiPerformancePredictionRepository aiPerformancePredictionRepository;
    private final HierarchyService hierarchyService;

    @Override
    public List<AiPredictionResponseDTO> getAllPredictions() {
        return aiPerformancePredictionRepository.findAll().stream().map(AiPredictionMapper::toResponseDTO).toList();
    }

    @Override
    public List<AiPredictionResponseDTO> getPredictionsByAgent(Long agentId) {
        return aiPerformancePredictionRepository.findByAgentId(agentId).stream().map(AiPredictionMapper::toResponseDTO).toList();
    }

    @Override
    public List<AiPredictionResponseDTO> getPredictionsByMonth(Integer year, Integer month) {
        return aiPerformancePredictionRepository.findByPredictionYearAndPredictionMonth(year, month).stream().map(AiPredictionMapper::toResponseDTO).toList();
    }

    @Override
    public List<AiPredictionResponseDTO> getMyTeamPredictions() {

        if (hierarchyService.isCurrentUserAdmin()) {
            return aiPerformancePredictionRepository.findAll().stream().map(AiPredictionMapper::toResponseDTO).toList();
        }

        List<Long> visibleIcIds = hierarchyService.getVisibleIcIdsForCurrentUser();

        if (visibleIcIds.isEmpty()) {
            return List.of();
        }

        return aiPerformancePredictionRepository.findByAgentIdIn(visibleIcIds).stream().map(AiPredictionMapper::toResponseDTO).toList();
    }

    @Override
    public List<AiPredictionResponseDTO> getMyTeamPredictionsByMonth(Integer year, Integer month) {

        if (hierarchyService.isCurrentUserAdmin()) {
            return aiPerformancePredictionRepository.findByPredictionYearAndPredictionMonth(year, month).stream().map(AiPredictionMapper::toResponseDTO).toList();
        }

        List<Long> visibleIcIds = hierarchyService.getVisibleIcIdsForCurrentUser();

        if (visibleIcIds.isEmpty()) {
            return List.of();
        }

        return aiPerformancePredictionRepository.findByAgentIdInAndPredictionYearAndPredictionMonth(visibleIcIds, year, month).stream().map(AiPredictionMapper::toResponseDTO).toList();
    }
}
