package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.ChatbotLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Long> {
}