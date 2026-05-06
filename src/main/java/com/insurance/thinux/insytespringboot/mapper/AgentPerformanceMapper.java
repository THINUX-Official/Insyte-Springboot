package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.AgentPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.model.AgentPerformance;
import com.insurance.thinux.insytespringboot.model.User;

public class AgentPerformanceMapper {

    private AgentPerformanceMapper() {
        // Prevent object creation
    }

    public static AgentPerformanceResponseDTO toResponseDTO(AgentPerformance performance) {

        if (performance == null) {
            return null;
        }

        User agent = performance.getAgent();
        User supervisor = agent != null ? agent.getSupervisor() : null;

        AgentPerformanceResponseDTO dto = new AgentPerformanceResponseDTO();

        dto.setId(performance.getId());

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

        dto.setPerformanceYear(performance.getPerformanceYear());
        dto.setPerformanceMonth(performance.getPerformanceMonth());

        dto.setTotalLeads(performance.getTotalLeads());
        dto.setConvertedLeads(performance.getConvertedLeads());
        dto.setCancelledLeads(performance.getCancelledLeads());
        dto.setOnHoldLeads(performance.getOnHoldLeads());

        dto.setTotalPremium(performance.getTotalPremium());
        dto.setTargetPremium(performance.getTargetPremium());
        dto.setTargetAchievementPercentage(performance.getTargetAchievementPercentage());

        dto.setConversionRate(performance.getConversionRate());
        dto.setAveragePremium(performance.getAveragePremium());
        dto.setPerformanceScore(performance.getPerformanceScore());

        dto.setCreatedAt(performance.getCreatedAt());
        dto.setUpdatedAt(performance.getUpdatedAt());

        return dto;
    }


}
