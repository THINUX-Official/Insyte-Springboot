package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.ChatbotRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.ChatbotResponseDTO;
import com.insurance.thinux.insytespringboot.service.ChatbotService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<StandardResponse<ChatbotResponseDTO>> ask(@RequestBody ChatbotRequestDTO requestDTO) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Chatbot response generated successfully", chatbotService.ask(requestDTO)));
    }
}