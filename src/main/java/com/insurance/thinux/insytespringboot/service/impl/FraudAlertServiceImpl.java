package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.FraudAlertStatusUpdateRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.FraudAlertResponseDTO;
import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import com.insurance.thinux.insytespringboot.mapper.FraudAlertMapper;
import com.insurance.thinux.insytespringboot.model.AiFraudAlert;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.AiFraudAlertRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.FraudAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudAlertServiceImpl implements FraudAlertService {

    private final AiFraudAlertRepository aiFraudAlertRepository;
    private final UserRepository userRepository;

    @Override
    public List<FraudAlertResponseDTO> getAllFraudAlerts() {
        return aiFraudAlertRepository.findAll().stream().map(FraudAlertMapper::toResponseDTO).toList();
    }

    @Override
    public List<FraudAlertResponseDTO> getOpenFraudAlerts() {
        return aiFraudAlertRepository.findByStatus(FraudAlertStatus.OPEN).stream().map(FraudAlertMapper::toResponseDTO).toList();
    }

    @Override
    public List<FraudAlertResponseDTO> getFraudAlertsByAgent(Long agentId) {
        return aiFraudAlertRepository.findByAgentId(agentId).stream().map(FraudAlertMapper::toResponseDTO).toList();
    }

    @Override
    public FraudAlertResponseDTO updateFraudAlertStatus(Long id, FraudAlertStatusUpdateRequestDTO requestDTO) {
        AiFraudAlert alert = aiFraudAlertRepository.findById(id).orElseThrow(() -> new RuntimeException("Fraud alert not found with id: " + id));

        if (requestDTO.getStatus() == null) {
            throw new RuntimeException("Fraud alert status is required");
        }

        alert.setStatus(requestDTO.getStatus());

        if (requestDTO.getReviewedById() != null) {
            User reviewedBy = userRepository.findById(requestDTO.getReviewedById()).orElseThrow(() -> new RuntimeException("Reviewer not found with id: " + requestDTO.getReviewedById()));

            alert.setReviewedBy(reviewedBy);
            alert.setReviewedAt(LocalDateTime.now());
        }

        AiFraudAlert updatedAlert = aiFraudAlertRepository.save(alert);

        return FraudAlertMapper.toResponseDTO(updatedAlert);
    }

}
