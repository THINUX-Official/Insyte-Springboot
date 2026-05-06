package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.RecommendationStatus;
import com.insurance.thinux.insytespringboot.model.AiRecommendation;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {

    List<AiRecommendation> findByAgent(User agent);

    List<AiRecommendation> findBySupervisor(User supervisor);

    List<AiRecommendation> findByAgentAndStatus(User agent, RecommendationStatus status);
}
