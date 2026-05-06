package com.insurance.thinux.insytespringboot.model;

import com.insurance.thinux.insytespringboot.enums.LocationPerformanceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "location_performance", uniqueConstraints = {@UniqueConstraint(name = "uk_location_performance_month", columnNames = {"province", "district", "performance_year", "performance_month"})})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LocationPerformance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String province;

    @Column(nullable = false, length = 50)
    private String district;

    @Column(name = "performance_year", nullable = false)
    private Integer performanceYear;

    @Column(name = "performance_month", nullable = false)
    private Integer performanceMonth;

    @Column(name = "total_leads")
    private Integer totalLeads = 0;

    @Column(name = "converted_leads")
    private Integer convertedLeads = 0;

    @Column(name = "total_premium", precision = 14, scale = 2)
    private BigDecimal totalPremium = BigDecimal.ZERO;

    @Column(name = "conversion_rate", precision = 6, scale = 2)
    private BigDecimal conversionRate = BigDecimal.ZERO;

    @Column(name = "average_premium", precision = 14, scale = 2)
    private BigDecimal averagePremium = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "performance_category", length = 20)
    private LocationPerformanceCategory performanceCategory = LocationPerformanceCategory.MEDIUM;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
