package com.insurance.thinux.insytespringboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insurance.thinux.insytespringboot.enums.*;
import com.insurance.thinux.insytespringboot.util.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: THINUX
 * @created: 08-Jan-26 - 12:16 PM
 */

@Entity
@Table(name = "leads")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Lead extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeadStatus status;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 12)
    private String nic;

    @Column(length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CivilStatus civilStatus;

    private LocalDate dob;

    @Column(nullable = false, length = 15)
    private String mobile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_id")
    private Occupation occupation;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Race race;

    @Column(length = 150)
    private String address1;

    @Column(length = 150)
    private String address2;

    @Column(length = 50)
    private String city;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private District district;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Province province;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Country country;

    /* Financial Data */
    @Column(precision = 12, scale = 2)
    private BigDecimal premium;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private LeadSource leadSource;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Probability probability;

    private LocalDate remindDate;

    @Lob // long text
    private String remark;

    @Column(length = 255)
    private String attachmentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_user_id", nullable = false)
    private User assignedUser;

    @Column(name = "converted_at")
    private LocalDateTime convertedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "last_followup_at")
    private LocalDateTime lastFollowupAt;

    @Column(name = "followup_count")
    private Integer followupCount = 0;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "duplicate_check_key")
    private String duplicateCheckKey;

    @OneToMany(mappedBy = "lead", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<LeadStatusHistory> statusHistories = new HashSet<>();

    @OneToMany(mappedBy = "lead", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<AiFraudAlert> fraudAlerts = new HashSet<>();
}
