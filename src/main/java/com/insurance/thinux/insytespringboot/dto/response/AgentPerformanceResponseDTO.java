package com.insurance.thinux.insytespringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AgentPerformanceResponseDTO {

    private Long id;

    private Long agentId;
    private String username;
    private String nickname;
    private String email;
    private String phone;

    private Long supervisorId;
    private String supervisorUsername;

    private Integer performanceYear;
    private Integer performanceMonth;

    private Integer totalLeads;
    private Integer convertedLeads;
    private Integer cancelledLeads;
    private Integer onHoldLeads;

    private BigDecimal totalPremium;
    private BigDecimal targetPremium;
    private BigDecimal targetAchievementPercentage;

    private BigDecimal conversionRate;
    private BigDecimal averagePremium;
    private BigDecimal performanceScore;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
