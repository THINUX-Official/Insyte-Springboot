package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.AiPredictionResponseDTO;
import com.insurance.thinux.insytespringboot.service.AiPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-predictions")
@RequiredArgsConstructor
public class AiPredictionController {

    private final AiPredictionService aiPredictionService;

    @GetMapping
    public List<AiPredictionResponseDTO> getAllPredictions() {
        return aiPredictionService.getAllPredictions();
    }

    @GetMapping("/agent/{agentId}")
    public List<AiPredictionResponseDTO> getPredictionsByAgent(@PathVariable Long agentId) {
        return aiPredictionService.getPredictionsByAgent(agentId);
    }

    @GetMapping("/month")
    public List<AiPredictionResponseDTO> getPredictionsByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return aiPredictionService.getPredictionsByMonth(year, month);
    }

    @GetMapping("/my-team")
    public List<AiPredictionResponseDTO> getMyTeamPredictions() {
        return aiPredictionService.getMyTeamPredictions();
    }

    @GetMapping("/my-team/month")
    public List<AiPredictionResponseDTO> getMyTeamPredictionsByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return aiPredictionService.getMyTeamPredictionsByMonth(year, month);
    }
}
