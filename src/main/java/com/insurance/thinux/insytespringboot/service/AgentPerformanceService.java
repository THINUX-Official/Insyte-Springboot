package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.response.AgentPerformanceResponseDTO;

import java.util.List;

public interface AgentPerformanceService {

    List<AgentPerformanceResponseDTO> getAllAgentPerformance();

    List<AgentPerformanceResponseDTO> getPerformanceByMonth(Integer year, Integer month);

    List<AgentPerformanceResponseDTO> getPerformanceByAgent(Long agentId);

    List<AgentPerformanceResponseDTO> generateMonthlyPerformance(Integer year, Integer month);

    List<AgentPerformanceResponseDTO> getMyTeamPerformance();

    List<AgentPerformanceResponseDTO> getMyTeamPerformanceByMonth(Integer year, Integer month);
}
