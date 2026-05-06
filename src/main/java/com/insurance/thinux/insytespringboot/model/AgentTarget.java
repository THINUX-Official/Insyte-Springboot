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
@Table(name = "agent_targets", uniqueConstraints = {@UniqueConstraint(name = "uk_agent_target_month", columnNames = {"agent_id", "target_year", "target_month"})})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AgentTarget extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Agent who receives the target.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    @Column(name = "target_year", nullable = false)
    private Integer targetYear;

    @Column(name = "target_month", nullable = false)
    private Integer targetMonth;

    @Column(name = "target_leads")
    private Integer targetLeads = 0;

    @Column(name = "target_conversions")
    private Integer targetConversions = 0;

    @Column(name = "target_premium", precision = 14, scale = 2)
    private BigDecimal targetPremium = BigDecimal.ZERO;

    /**
     * Supervisor/admin who assigned the target.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User assignedBy;
}
