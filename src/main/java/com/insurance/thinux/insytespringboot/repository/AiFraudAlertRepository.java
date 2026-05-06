package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import com.insurance.thinux.insytespringboot.model.AiFraudAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiFraudAlertRepository extends JpaRepository<AiFraudAlert, Long> {

    List<AiFraudAlert> findByAgentId(Long agentId);

    List<AiFraudAlert> findByStatus(FraudAlertStatus status);
}
