package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import com.insurance.thinux.insytespringboot.model.AiFraudAlert;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AiFraudAlertRepository extends JpaRepository<AiFraudAlert, Long> {

    List<AiFraudAlert> findByAgent(User agent);

    List<AiFraudAlert> findByStatus(FraudAlertStatus status);
}
