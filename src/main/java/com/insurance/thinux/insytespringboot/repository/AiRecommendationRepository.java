package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.RecommendationStatus;
import com.insurance.thinux.insytespringboot.model.AiRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    List<AiRecommendation> findByAgentId(Long agentId);

    List<AiRecommendation> findByAgentIdIn(List<Long> agentIds);

    List<AiRecommendation> findBySupervisorId(Long supervisorId);

    List<AiRecommendation> findBySupervisorIdIn(List<Long> supervisorIds);

    List<AiRecommendation> findByAgentIdAndStatus(Long agentId, RecommendationStatus status);
}