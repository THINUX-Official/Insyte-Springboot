package com.insurance.thinux.insytespringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponseDTO {

    private String reply;
    private Double confidenceScore;
    private String matchedQuestion;
    private String matchedKeywords;
}