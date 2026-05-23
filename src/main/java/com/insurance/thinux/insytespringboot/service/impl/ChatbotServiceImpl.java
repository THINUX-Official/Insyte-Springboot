package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.ChatbotRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.ChatbotResponseDTO;
import com.insurance.thinux.insytespringboot.model.ChatbotFaq;
import com.insurance.thinux.insytespringboot.model.ChatbotLog;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.ChatbotFaqRepository;
import com.insurance.thinux.insytespringboot.repository.ChatbotLogRepository;
import com.insurance.thinux.insytespringboot.service.ChatbotService;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private static final double MIN_ACCEPTABLE_SCORE = 20.0;

    private final ChatbotFaqRepository chatbotFaqRepository;
    private final ChatbotLogRepository chatbotLogRepository;
    private final HierarchyService hierarchyService;

    private final Set<String> stopWords = Set.of("a", "an", "the", "is", "are", "am", "to", "of", "for", "in", "on", "at", "and", "or", "how", "what", "why", "when", "where", "can", "i", "me", "my", "you", "your", "please", "tell", "show", "get", "give", "do", "does", "did");

    @Override
    public ChatbotResponseDTO ask(ChatbotRequestDTO requestDTO) {
        String message = requestDTO == null ? null : requestDTO.getMessage();

        if (message == null || message.trim().isBlank()) {
            ChatbotResponseDTO response = new ChatbotResponseDTO("Please type a message so I can help you.", 0.0, null, null);

            saveLog(message, response);
            return response;
        }

        List<ChatbotFaq> faqs = chatbotFaqRepository.findByStatusIgnoreCaseOrderByIdDesc("ACTIVE");

        if (faqs.isEmpty()) {
            ChatbotResponseDTO response = new ChatbotResponseDTO("I do not have FAQ data configured yet. Please contact the administrator.", 0.0, null, null);

            saveLog(message, response);
            return response;
        }

        MatchResult bestMatch = findBestMatch(message, faqs);

        ChatbotResponseDTO response;

        if (bestMatch == null || bestMatch.score < MIN_ACCEPTABLE_SCORE) {
            response = new ChatbotResponseDTO("Sorry, I could not find a confident answer for that. Please ask about leads, performance, fraud alerts, recommendations, reports, or user roles.", bestMatch == null ? 0.0 : round(bestMatch.score), bestMatch == null ? null : bestMatch.faq.getQuestion(), bestMatch == null ? null : bestMatch.faq.getKeywords());
        } else {
            response = new ChatbotResponseDTO(bestMatch.faq.getAnswer(), round(bestMatch.score), bestMatch.faq.getQuestion(), bestMatch.faq.getKeywords());
        }

        saveLog(message, response);
        return response;
    }

    private MatchResult findBestMatch(String message, List<ChatbotFaq> faqs) {
        Set<String> messageTokens = tokenize(message);

        MatchResult bestMatch = null;

        for (ChatbotFaq faq : faqs) {
            double questionScore = calculateTokenSimilarity(messageTokens, tokenize(faq.getQuestion()));
            double keywordScore = calculateTokenSimilarity(messageTokens, tokenize(faq.getKeywords()));

            double finalScore = (questionScore * 0.65) + (keywordScore * 0.35);

            if (bestMatch == null || finalScore > bestMatch.score) {
                bestMatch = new MatchResult(faq, finalScore);
            }
        }

        return bestMatch;
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(text.toLowerCase().replaceAll("[^a-zA-Z0-9 ]", " ").split("\\s+")).map(String::trim).filter(token -> !token.isBlank()).filter(token -> !stopWords.contains(token)).collect(Collectors.toSet());
    }

    private double calculateTokenSimilarity(Set<String> messageTokens, Set<String> targetTokens) {
        if (messageTokens.isEmpty() || targetTokens.isEmpty()) {
            return 0.0;
        }

        long matchedCount = messageTokens.stream().filter(targetTokens::contains).count();

        double precision = (matchedCount * 100.0) / messageTokens.size();
        double recall = (matchedCount * 100.0) / targetTokens.size();

        return (precision * 0.6) + (recall * 0.4);
    }

    private void saveLog(String message, ChatbotResponseDTO response) {
        String username = getCurrentUsernameSafely();

        ChatbotLog log = ChatbotLog.builder().username(username).userMessage(message).botReply(response.getReply()).matchedQuestion(response.getMatchedQuestion()).confidenceScore(response.getConfidenceScore()).build();

        chatbotLogRepository.save(log);
    }

    private String getCurrentUsernameSafely() {
        try {
            User user = hierarchyService.getCurrentUser();

            if (user != null && user.getUsername() != null) {
                return user.getUsername();
            }
        } catch (Exception ignored) {
            return "anonymous";
        }

        return "anonymous";
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private record MatchResult(ChatbotFaq faq, double score) {
    }
}