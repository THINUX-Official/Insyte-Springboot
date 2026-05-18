package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.AgentPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.service.AgentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-performance")
@RequiredArgsConstructor
public class AgentPerformanceController {

    private final AgentPerformanceService agentPerformanceService;

    @GetMapping
    public List<AgentPerformanceResponseDTO> getAllAgentPerformance() {
        return agentPerformanceService.getAllAgentPerformance();
    }

    @GetMapping("/month")
    public List<AgentPerformanceResponseDTO> getPerformanceByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return agentPerformanceService.getPerformanceByMonth(year, month);
    }

    @GetMapping("/agent/{agentId}")
    public List<AgentPerformanceResponseDTO> getPerformanceByAgent(@PathVariable Long agentId) {
        return agentPerformanceService.getPerformanceByAgent(agentId);
    }

    @PostMapping("/generate")
    public List<AgentPerformanceResponseDTO> generateMonthlyPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        return agentPerformanceService.generateMonthlyPerformance(year, month);
    }

    @GetMapping("/my-team")
    public List<AgentPerformanceResponseDTO> getMyTeamPerformance() {
        return agentPerformanceService.getMyTeamPerformance();
    }

    @GetMapping("/my-team/month")
    public List<AgentPerformanceResponseDTO> getMyTeamPerformanceByMonth(@RequestParam Integer year, @RequestParam Integer month) {
        return agentPerformanceService.getMyTeamPerformanceByMonth(year, month);
    }
}
