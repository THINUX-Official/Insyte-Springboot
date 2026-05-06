package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "agent_performance", uniqueConstraints = {@UniqueConstraint(name = "uk_agent_performance_month", columnNames = {"agent_id", "performance_year", "performance_month"})})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgentPerformance extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Agent whose performance is calculated.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    @Column(name = "performance_year", nullable = false)
    private Integer performanceYear;

    @Column(name = "performance_month", nullable = false)
    private Integer performanceMonth;

    @Column(name = "total_leads")
    private Integer totalLeads = 0;

    @Column(name = "converted_leads")
    private Integer convertedLeads = 0;

    @Column(name = "cancelled_leads")
    private Integer cancelledLeads = 0;

    @Column(name = "on_hold_leads")
    private Integer onHoldLeads = 0;

    @Column(name = "total_premium", precision = 14, scale = 2)
    private BigDecimal totalPremium = BigDecimal.ZERO;

    @Column(name = "target_premium", precision = 14, scale = 2)
    private BigDecimal targetPremium = BigDecimal.ZERO;

    @Column(name = "target_achievement_percentage", precision = 6, scale = 2)
    private BigDecimal targetAchievementPercentage = BigDecimal.ZERO;

    @Column(name = "conversion_rate", precision = 6, scale = 2)
    private BigDecimal conversionRate = BigDecimal.ZERO;

    @Column(name = "average_premium", precision = 14, scale = 2)
    private BigDecimal averagePremium = BigDecimal.ZERO;

    @Column(name = "performance_score", precision = 6, scale = 2)
    private BigDecimal performanceScore = BigDecimal.ZERO;
}
