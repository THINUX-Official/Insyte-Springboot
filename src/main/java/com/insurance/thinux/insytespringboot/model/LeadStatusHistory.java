package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lead_status_history")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeadStatusHistory extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Related lead.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id", nullable = false)
    @JsonIgnoreProperties({"assignedUser", "occupation"})
    private Lead lead;

    /**
     * Agent responsible for the lead.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User agent;

    @Column(name = "old_status", length = 50)
    private String oldStatus;

    @Column(name = "new_status", nullable = false, length = 50)
    private String newStatus;

    @Column(columnDefinition = "TEXT")
    private String remark;

    /**
     * User who changed the status.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    @JsonIgnoreProperties({"password", "roles", "leads", "subordinates", "supervisor"})
    private User changedBy;
}
