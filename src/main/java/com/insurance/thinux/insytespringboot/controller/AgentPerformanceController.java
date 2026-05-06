package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.model.AgentPerformance;
import com.insurance.thinux.insytespringboot.repository.AgentPerformanceRepository;
import com.insurance.thinux.insytespringboot.service.impl.AgentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent-performance")
@RequiredArgsConstructor
public class AgentPerformanceController {

    private final AgentPerformanceService agentPerformanceService;
    private final AgentPerformanceRepository agentPerformanceRepository;

    @PostMapping("/generate")
    public String generateMonthlyPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        agentPerformanceService.generateMonthlyPerformance(year, month);
        return "Monthly agent performance generated successfully";
    }

    @GetMapping
    public List<AgentPerformance> getAllPerformance() {
        return agentPerformanceRepository.findAll();
    }

    @GetMapping("/month")
    public List<AgentPerformance> getMonthlyPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        return agentPerformanceRepository.findByPerformanceYearAndPerformanceMonth(year, month);
    }
}
