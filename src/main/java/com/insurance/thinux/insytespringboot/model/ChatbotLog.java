package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_logs")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatbotLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Optional logged-in user who used the chatbot.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User user;

    @Column(name = "user_message", nullable = false, columnDefinition = "TEXT")
    private String userMessage;

    @Column(name = "bot_response", nullable = false, columnDefinition = "TEXT")
    private String botResponse;

    @Column(name = "confidence_score", precision = 6, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
