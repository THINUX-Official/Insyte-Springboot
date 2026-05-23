package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.LocationPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.service.LocationPerformanceService;
import com.insurance.thinux.insytespringboot.util.StandardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location-performance")
@RequiredArgsConstructor
@CrossOrigin
public class LocationPerformanceController {

    private final LocationPerformanceService locationPerformanceService;

    @PostMapping("/generate")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> generateLocationPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Location performance generated successfully", locationPerformanceService.generateLocationPerformance(year, month)));
    }

    @GetMapping("/month")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getLocationPerformanceByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Location performance fetched successfully", locationPerformanceService.getLocationPerformanceByMonth(year, month)));
    }

    @GetMapping("/top")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getTopLocations(@RequestParam Integer year, @RequestParam Integer month, @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Top performing locations fetched successfully", locationPerformanceService.getTopLocations(year, month, limit)));
    }

    @GetMapping("/bottom")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getBottomLocations(@RequestParam Integer year, @RequestParam Integer month, @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Least performing locations fetched successfully", locationPerformanceService.getBottomLocations(year, month, limit)));
    }

    @GetMapping("/my-team/month")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getMyTeamLocationPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Team location performance fetched successfully", locationPerformanceService.getMyTeamLocationPerformance(year, month)));
    }

    @GetMapping("/my-team/top")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getMyTeamTopLocations(@RequestParam Integer year, @RequestParam Integer month, @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Team top performing locations fetched successfully", locationPerformanceService.getMyTeamTopLocations(year, month, limit)));
    }

    @GetMapping("/my-team/bottom")
    public ResponseEntity<StandardResponse<List<LocationPerformanceResponseDTO>>> getMyTeamBottomLocations(@RequestParam Integer year, @RequestParam Integer month, @RequestParam(defaultValue = "5") Integer limit) {
        return ResponseEntity.ok(new StandardResponse<>(200, "Team least performing locations fetched successfully", locationPerformanceService.getMyTeamBottomLocations(year, month, limit)));
    }
}