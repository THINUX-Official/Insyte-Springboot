package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.ChatbotLog;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotLogRepository extends JpaRepository<ChatbotLog, Long> {

    List<ChatbotLog> findByUser(User user);
}
