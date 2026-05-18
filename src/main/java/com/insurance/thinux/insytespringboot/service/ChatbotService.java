package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.ChatbotRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.ChatbotResponseDTO;

public interface ChatbotService {

    ChatbotResponseDTO ask(ChatbotRequestDTO requestDTO);
}