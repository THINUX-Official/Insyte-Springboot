package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.FraudAlertStatusUpdateRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.FraudAlertResponseDTO;

import java.util.List;

public interface FraudAlertService {

    List<FraudAlertResponseDTO> getAllFraudAlerts();

    List<FraudAlertResponseDTO> getOpenFraudAlerts();

    List<FraudAlertResponseDTO> getFraudAlertsByAgent(Long agentId);

    List<FraudAlertResponseDTO> getMyTeamFraudAlerts();

    List<FraudAlertResponseDTO> getMyTeamOpenFraudAlerts();

    FraudAlertResponseDTO updateFraudAlertStatus(Long id, FraudAlertStatusUpdateRequestDTO requestDTO);
}