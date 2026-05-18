package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.LocationPerformanceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationPerformanceResponseDTO {

    private Long id;

    private String province;
    private String district;

    private Integer performanceYear;
    private Integer performanceMonth;

    private Integer totalLeads;
    private Integer convertedLeads;

    private BigDecimal totalPremium;
    private BigDecimal conversionRate;
    private BigDecimal averagePremium;

    private LocationPerformanceCategory performanceCategory;
}