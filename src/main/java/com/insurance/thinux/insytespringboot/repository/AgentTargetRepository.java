package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.AgentTarget;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AgentTargetRepository extends JpaRepository<AgentTarget, Long> {

    List<AgentTarget> findByAgent(User agent);

    Optional<AgentTarget> findByAgentAndTargetYearAndTargetMonth(User agent, Integer targetYear, Integer targetMonth);

    List<AgentTarget> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);
}
