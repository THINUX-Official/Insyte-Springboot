package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.RecommendationResponseDTO;
import com.insurance.thinux.insytespringboot.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping
    public List<RecommendationResponseDTO> getAllRecommendations() {
        return recommendationService.getAllRecommendations();
    }

    @GetMapping("/agent/{agentId}")
    public List<RecommendationResponseDTO> getRecommendationsByAgent(@PathVariable Long agentId) {
        return recommendationService.getRecommendationsByAgent(agentId);
    }

    @GetMapping("/supervisor/{supervisorId}")
    public List<RecommendationResponseDTO> getRecommendationsBySupervisor(@PathVariable Long supervisorId) {
        return recommendationService.getRecommendationsBySupervisor(supervisorId);
    }

    @PutMapping("/{id}/view")
    public RecommendationResponseDTO markRecommendationAsViewed(@PathVariable Long id) {
        return recommendationService.markRecommendationAsViewed(id);
    }
}
