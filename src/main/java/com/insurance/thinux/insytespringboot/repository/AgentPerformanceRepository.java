package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.AgentPerformance;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentPerformanceRepository extends JpaRepository<AgentPerformance, Long> {

    List<AgentPerformance> findByAgent(User agent);

    List<AgentPerformance> findByAgentId(Long agentId);

    Optional<AgentPerformance> findByAgentAndPerformanceYearAndPerformanceMonth(User agent, Integer performanceYear, Integer performanceMonth);

    List<AgentPerformance> findByPerformanceYearAndPerformanceMonth(Integer performanceYear, Integer performanceMonth);
}
