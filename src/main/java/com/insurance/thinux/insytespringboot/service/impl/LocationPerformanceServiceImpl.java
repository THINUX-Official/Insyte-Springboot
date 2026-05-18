package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.LocationPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.enums.LeadStatus;
import com.insurance.thinux.insytespringboot.enums.LocationPerformanceCategory;
import com.insurance.thinux.insytespringboot.mapper.LocationPerformanceMapper;
import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.LocationPerformance;
import com.insurance.thinux.insytespringboot.repository.LeadRepository;
import com.insurance.thinux.insytespringboot.repository.LocationPerformanceRepository;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import com.insurance.thinux.insytespringboot.service.LocationPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationPerformanceServiceImpl implements LocationPerformanceService {

    private final LocationPerformanceRepository locationPerformanceRepository;
    private final LeadRepository leadRepository;
    private final HierarchyService hierarchyService;

    @Override
    public List<LocationPerformanceResponseDTO> generateLocationPerformance(Integer year, Integer month) {
        validateYearMonth(year, month);

        List<Lead> leads = leadRepository.findAll();

        List<LocationPerformance> generatedRecords = buildLocationPerformanceFromLeads(leads, year, month);

        List<LocationPerformance> savedRecords = generatedRecords.stream().map(this::upsertLocationPerformance).toList();

        return savedRecords.stream().sorted(Comparator.comparing(LocationPerformance::getConversionRate).reversed()).map(LocationPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getLocationPerformanceByMonth(Integer year, Integer month) {
        validateYearMonth(year, month);

        return locationPerformanceRepository.findByPerformanceYearAndPerformanceMonthOrderByConversionRateDesc(year, month).stream().map(LocationPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getTopLocations(Integer year, Integer month, Integer limit) {
        validateYearMonth(year, month);

        int safeLimit = safeLimit(limit);

        return locationPerformanceRepository.findByPerformanceYearAndPerformanceMonthOrderByConversionRateDesc(year, month).stream().limit(safeLimit).map(LocationPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getBottomLocations(Integer year, Integer month, Integer limit) {
        validateYearMonth(year, month);

        int safeLimit = safeLimit(limit);

        return locationPerformanceRepository.findByPerformanceYearAndPerformanceMonthOrderByConversionRateAsc(year, month).stream().limit(safeLimit).map(LocationPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getMyTeamLocationPerformance(Integer year, Integer month) {
        validateYearMonth(year, month);

        List<Lead> scopedLeads;

        if (hierarchyService.isCurrentUserAdmin()) {
            scopedLeads = leadRepository.findAll();
        } else {
            List<Long> visibleUserIds = hierarchyService.getVisibleUserIdsForCurrentUser();

            if (visibleUserIds.isEmpty()) {
                return List.of();
            }

            scopedLeads = leadRepository.findByAssignedUserIdInOrderByIdDesc(visibleUserIds);
        }

        return buildLocationPerformanceFromLeads(scopedLeads, year, month).stream().sorted(Comparator.comparing(LocationPerformance::getConversionRate).reversed()).map(LocationPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getMyTeamTopLocations(Integer year, Integer month, Integer limit) {
        return getMyTeamLocationPerformance(year, month).stream().limit(safeLimit(limit)).toList();
    }

    @Override
    public List<LocationPerformanceResponseDTO> getMyTeamBottomLocations(Integer year, Integer month, Integer limit) {
        return getMyTeamLocationPerformance(year, month).stream().sorted(Comparator.comparing(LocationPerformanceResponseDTO::getConversionRate)).limit(safeLimit(limit)).toList();
    }

    private List<LocationPerformance> buildLocationPerformanceFromLeads(List<Lead> leads, Integer year, Integer month) {
        Map<String, LocationAccumulator> grouped = new LinkedHashMap<>();

        for (Lead lead : leads) {
            if (!isLeadInMonth(lead, year, month)) {
                continue;
            }

            String province = lead.getProvince() == null ? "UNKNOWN" : lead.getProvince().name();
            String district = lead.getDistrict() == null ? "UNKNOWN" : lead.getDistrict().name();

            String key = province + "|" + district;

            LocationAccumulator accumulator = grouped.computeIfAbsent(key, k -> new LocationAccumulator(province, district));

            accumulator.totalLeads++;

            if (lead.getStatus() == LeadStatus.COMPLETED) {
                accumulator.convertedLeads++;

                if (lead.getPremium() != null) {
                    accumulator.totalPremium = accumulator.totalPremium.add(lead.getPremium());
                }
            }
        }

        return grouped.values().stream().map(acc -> acc.toEntity(year, month)).toList();
    }

    private boolean isLeadInMonth(Lead lead, Integer year, Integer month) {
        if (lead.getCreatedAt() == null) {
            return false;
        }

        return lead.getCreatedAt().getYear() == year && lead.getCreatedAt().getMonthValue() == month;
    }

    private LocationPerformance upsertLocationPerformance(LocationPerformance generated) {
        LocationPerformance entity = locationPerformanceRepository.findByProvinceAndDistrictAndPerformanceYearAndPerformanceMonth(generated.getProvince(), generated.getDistrict(), generated.getPerformanceYear(), generated.getPerformanceMonth()).orElseGet(LocationPerformance::new);

        entity.setProvince(generated.getProvince());
        entity.setDistrict(generated.getDistrict());
        entity.setPerformanceYear(generated.getPerformanceYear());
        entity.setPerformanceMonth(generated.getPerformanceMonth());
        entity.setTotalLeads(generated.getTotalLeads());
        entity.setConvertedLeads(generated.getConvertedLeads());
        entity.setTotalPremium(generated.getTotalPremium());
        entity.setConversionRate(generated.getConversionRate());
        entity.setAveragePremium(generated.getAveragePremium());
        entity.setPerformanceCategory(generated.getPerformanceCategory());

        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }

        return locationPerformanceRepository.save(entity);
    }

    private void validateYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            throw new RuntimeException("Year and month are required");
        }

        if (month < 1 || month > 12) {
            throw new RuntimeException("Month must be between 1 and 12");
        }
    }

    private int safeLimit(Integer limit) {
        if (limit == null || limit <= 0) {
            return 5;
        }

        return Math.min(limit, 20);
    }

    private static class LocationAccumulator {
        private final String province;
        private final String district;

        private int totalLeads = 0;
        private int convertedLeads = 0;
        private BigDecimal totalPremium = BigDecimal.ZERO;

        private LocationAccumulator(String province, String district) {
            this.province = province;
            this.district = district;
        }

        private LocationPerformance toEntity(Integer year, Integer month) {
            BigDecimal conversionRate = BigDecimal.ZERO;
            BigDecimal averagePremium = BigDecimal.ZERO;

            if (totalLeads > 0) {
                conversionRate = BigDecimal.valueOf(convertedLeads).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(totalLeads), 2, RoundingMode.HALF_UP);
            }

            if (convertedLeads > 0) {
                averagePremium = totalPremium.divide(BigDecimal.valueOf(convertedLeads), 2, RoundingMode.HALF_UP);
            }

            LocationPerformanceCategory category = getCategory(conversionRate);

            LocationPerformance locationPerformance = new LocationPerformance();
            locationPerformance.setProvince(province);
            locationPerformance.setDistrict(district);
            locationPerformance.setPerformanceYear(year);
            locationPerformance.setPerformanceMonth(month);
            locationPerformance.setTotalLeads(totalLeads);
            locationPerformance.setConvertedLeads(convertedLeads);
            locationPerformance.setTotalPremium(totalPremium);
            locationPerformance.setConversionRate(conversionRate);
            locationPerformance.setAveragePremium(averagePremium);
            locationPerformance.setPerformanceCategory(category);
            locationPerformance.setCreatedAt(LocalDateTime.now());

            return locationPerformance;
        }

        private LocationPerformanceCategory getCategory(BigDecimal conversionRate) {
            if (conversionRate.compareTo(BigDecimal.valueOf(60)) >= 0) {
                return LocationPerformanceCategory.HIGH;
            }

            if (conversionRate.compareTo(BigDecimal.valueOf(30)) >= 0) {
                return LocationPerformanceCategory.MEDIUM;
            }

            return LocationPerformanceCategory.LOW;
        }
    }
}