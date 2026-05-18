package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.response.LocationPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.model.LocationPerformance;

public class LocationPerformanceMapper {

    private LocationPerformanceMapper() {
    }

    public static LocationPerformanceResponseDTO toResponseDTO(LocationPerformance entity) {
        if (entity == null) {
            return null;
        }

        return new LocationPerformanceResponseDTO(entity.getId(), entity.getProvince(), entity.getDistrict(), entity.getPerformanceYear(), entity.getPerformanceMonth(), entity.getTotalLeads(), entity.getConvertedLeads(), entity.getTotalPremium(), entity.getConversionRate(), entity.getAveragePremium(), entity.getPerformanceCategory());
    }
}