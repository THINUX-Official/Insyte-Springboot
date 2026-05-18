package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.LocationPerformance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationPerformanceRepository extends JpaRepository<LocationPerformance, Long> {

    List<LocationPerformance> findByPerformanceYearAndPerformanceMonthOrderByConversionRateDesc(Integer performanceYear, Integer performanceMonth);

    List<LocationPerformance> findByPerformanceYearAndPerformanceMonthOrderByConversionRateAsc(Integer performanceYear, Integer performanceMonth);

    List<LocationPerformance> findByPerformanceYearAndPerformanceMonth(Integer performanceYear, Integer performanceMonth);

    Optional<LocationPerformance> findByProvinceAndDistrictAndPerformanceYearAndPerformanceMonth(String province, String district, Integer performanceYear, Integer performanceMonth);
}