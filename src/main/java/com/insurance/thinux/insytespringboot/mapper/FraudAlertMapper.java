package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.FraudAlertResponseDTO;
import com.insurance.thinux.insytespringboot.model.AiFraudAlert;
import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.User;

public class FraudAlertMapper {

    private FraudAlertMapper() {
        // Prevent object creation
    }

    public static FraudAlertResponseDTO toResponseDTO(AiFraudAlert alert) {

        if (alert == null) {
            return null;
        }

        User agent = alert.getAgent();
        User supervisor = agent != null ? agent.getSupervisor() : null;
        User reviewedBy = alert.getReviewedBy();
        Lead lead = alert.getLead();

        FraudAlertResponseDTO dto = new FraudAlertResponseDTO();

        dto.setId(alert.getId());

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

        if (lead != null) {
            dto.setLeadId(lead.getId());
            dto.setLeadName(lead.getName());
            dto.setLeadMobile(lead.getMobile());

            if (lead.getStatus() != null) {
                dto.setLeadStatus(lead.getStatus().name());
            }
        }

        dto.setAlertType(alert.getAlertType());
        dto.setSeverity(alert.getSeverity());
        dto.setAnomalyScore(alert.getAnomalyScore());

        dto.setDescription(alert.getDescription());
        dto.setStatus(alert.getStatus());
        dto.setDetectedAt(alert.getDetectedAt());

        if (reviewedBy != null) {
            dto.setReviewedById(reviewedBy.getId());
            dto.setReviewedByUsername(reviewedBy.getUsername());
        }

        dto.setReviewedAt(alert.getReviewedAt());

        return dto;
    }
}
