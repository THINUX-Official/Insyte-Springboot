package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.enums.AlertSeverity;
import com.insurance.thinux.insytespringboot.enums.FraudAlertStatus;
import com.insurance.thinux.insytespringboot.enums.FraudAlertType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_fraud_alerts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AiFraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Agent related to the suspicious activity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    /**
     * Optional related lead.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    @JsonIgnoreProperties({"assignedUser", "occupation"})
    private Lead lead;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false, length = 50)
    private FraudAlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private AlertSeverity severity = AlertSeverity.LOW;

    @Column(name = "anomaly_score", precision = 8, scale = 4)
    private BigDecimal anomalyScore;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private FraudAlertStatus status = FraudAlertStatus.OPEN;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt = LocalDateTime.now();

    /**
     * Admin/supervisor who reviewed the alert.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;
}
