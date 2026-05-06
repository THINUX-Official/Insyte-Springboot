package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.AiPredictionResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.AiPredictionMapper;
import com.insurance.thinux.insytespringboot.repository.AiPerformancePredictionRepository;
import com.insurance.thinux.insytespringboot.service.AiPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiPredictionServiceImpl implements AiPredictionService {

    private final AiPerformancePredictionRepository aiPerformancePredictionRepository;

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
}
