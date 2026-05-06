package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.PerformancePredictionLabel;
import com.insurance.thinux.insytespringboot.enums.PerformanceRiskLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AiPredictionResponseDTO {

    private Long id;

    private Long agentId;
    private String username;
    private String nickname;
    private String email;
    private String phone;

    private Long supervisorId;
    private String supervisorUsername;

    private Integer predictionYear;
    private Integer predictionMonth;

    private BigDecimal predictedTotalPremium;
    private BigDecimal predictedConversionRate;
    private BigDecimal predictedPerformanceScore;

    private PerformanceRiskLevel riskLevel;
    private PerformancePredictionLabel predictionLabel;

    private String modelName;
    private String modelVersion;
    private BigDecimal confidenceScore;

    private LocalDateTime generatedAt;
}
