package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.response.AiPredictionResponseDTO;

import java.util.List;

public interface AiPredictionService {

    List<AiPredictionResponseDTO> getAllPredictions();

    List<AiPredictionResponseDTO> getPredictionsByAgent(Long agentId);

    List<AiPredictionResponseDTO> getPredictionsByMonth(Integer year, Integer month);
}
