package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.AlertSeverity;
import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import com.insurance.thinux.insytespringboot.enums.FraudAlertType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FraudAlertResponseDTO {

    private Long id;

    private Long agentId;
    private String username;
    private String nickname;
    private String email;
    private String phone;

    private Long supervisorId;
    private String supervisorUsername;

    private Long leadId;
    private String leadName;
    private String leadMobile;
    private String leadStatus;

    private FraudAlertType alertType;
    private AlertSeverity severity;
    private BigDecimal anomalyScore;

    private String description;
    private FraudAlertStatus status;

    private LocalDateTime detectedAt;

    private Long reviewedById;
    private String reviewedByUsername;
    private LocalDateTime reviewedAt;
}
