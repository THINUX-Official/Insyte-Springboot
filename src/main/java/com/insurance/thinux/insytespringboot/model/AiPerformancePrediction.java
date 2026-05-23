package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.enums.PerformancePredictionLabel;
import com.insurance.thinux.insytespringboot.enums.PerformanceRiskLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_performance_predictions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AiPerformancePrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Agent for whom the prediction is generated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    @Column(name = "prediction_year", nullable = false)
    private Integer predictionYear;

    @Column(name = "prediction_month", nullable = false)
    private Integer predictionMonth;

    @Column(name = "predicted_total_premium", precision = 14, scale = 2)
    private BigDecimal predictedTotalPremium = BigDecimal.ZERO;

    @Column(name = "predicted_conversion_rate", precision = 6, scale = 2)
    private BigDecimal predictedConversionRate = BigDecimal.ZERO;

    @Column(name = "predicted_performance_score", precision = 6, scale = 2)
    private BigDecimal predictedPerformanceScore = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 20)
    private PerformanceRiskLevel riskLevel = PerformanceRiskLevel.LOW;

    @Enumerated(EnumType.STRING)
    @Column(name = "prediction_label", length = 50)
    private PerformancePredictionLabel predictionLabel;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(name = "confidence_score", precision = 6, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt = LocalDateTime.now();
}
