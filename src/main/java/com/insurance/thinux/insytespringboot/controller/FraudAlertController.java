package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.FraudAlertStatusUpdateRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.FraudAlertResponseDTO;
import com.insurance.thinux.insytespringboot.service.FraudAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud-alerts")
@RequiredArgsConstructor
public class FraudAlertController {

    private final FraudAlertService fraudAlertService;

    @GetMapping
    public List<FraudAlertResponseDTO> getAllFraudAlerts() {
        return fraudAlertService.getAllFraudAlerts();
    }

    @GetMapping("/my-team")
    public List<FraudAlertResponseDTO> getMyTeamFraudAlerts() {
        return fraudAlertService.getMyTeamFraudAlerts();
    }

    @GetMapping("/open")
    public List<FraudAlertResponseDTO> getOpenFraudAlerts() {
        return fraudAlertService.getOpenFraudAlerts();
    }

    @GetMapping("/my-team/open")
    public List<FraudAlertResponseDTO> getMyTeamOpenFraudAlerts() {
        return fraudAlertService.getMyTeamOpenFraudAlerts();
    }

    @GetMapping("/agent/{agentId}")
    public List<FraudAlertResponseDTO> getFraudAlertsByAgent(@PathVariable Long agentId) {
        return fraudAlertService.getFraudAlertsByAgent(agentId);
    }

    @PutMapping("/{id}/status")
    public FraudAlertResponseDTO updateFraudAlertStatus(@PathVariable Long id, @RequestBody FraudAlertStatusUpdateRequestDTO requestDTO) {
        return fraudAlertService.updateFraudAlertStatus(id, requestDTO);
    }
}