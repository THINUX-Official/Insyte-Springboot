package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.response.LocationPerformanceResponseDTO;

import java.util.List;

public interface LocationPerformanceService {

    List<LocationPerformanceResponseDTO> generateLocationPerformance(Integer year, Integer month);

    List<LocationPerformanceResponseDTO> getLocationPerformanceByMonth(Integer year, Integer month);

    List<LocationPerformanceResponseDTO> getTopLocations(Integer year, Integer month, Integer limit);

    List<LocationPerformanceResponseDTO> getBottomLocations(Integer year, Integer month, Integer limit);

    List<LocationPerformanceResponseDTO> getMyTeamLocationPerformance(Integer year, Integer month);

    List<LocationPerformanceResponseDTO> getMyTeamTopLocations(Integer year, Integer month, Integer limit);

    List<LocationPerformanceResponseDTO> getMyTeamBottomLocations(Integer year, Integer month, Integer limit);
}