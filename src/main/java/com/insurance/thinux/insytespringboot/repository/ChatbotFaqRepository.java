package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.ChatbotFaq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatbotFaqRepository extends JpaRepository<ChatbotFaq, Long> {

    List<ChatbotFaq> findByStatusIgnoreCaseOrderByIdDesc(String status);
}