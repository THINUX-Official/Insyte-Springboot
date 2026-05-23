package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.enums.RecommendationPriority;
import com.insurance.thinux.insytespringboot.enums.RecommendationStatus;
import com.insurance.thinux.insytespringboot.enums.RecommendationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_recommendations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AiRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Agent who receives the recommendation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    /**
     * Optional supervisor connected to the recommendation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User supervisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false, length = 50)
    private RecommendationType recommendationType;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "recommendation_text", nullable = false, columnDefinition = "TEXT")
    private String recommendationText;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RecommendationPriority priority = RecommendationPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private RecommendationStatus status = RecommendationStatus.NEW;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;
}
