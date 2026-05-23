package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.LeadStatusHistory;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadStatusHistoryRepository extends JpaRepository<LeadStatusHistory, Long> {

    List<LeadStatusHistory> findByLead(Lead lead);

    List<LeadStatusHistory> findByAgent(User agent);
}
