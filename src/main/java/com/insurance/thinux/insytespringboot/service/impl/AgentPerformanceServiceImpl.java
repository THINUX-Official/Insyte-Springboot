package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.AgentPerformanceResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.AgentPerformanceMapper;
import com.insurance.thinux.insytespringboot.model.AgentPerformance;
import com.insurance.thinux.insytespringboot.model.AgentTarget;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.AgentPerformanceRepository;
import com.insurance.thinux.insytespringboot.repository.AgentTargetRepository;
import com.insurance.thinux.insytespringboot.repository.LeadRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.AgentPerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentPerformanceServiceImpl implements AgentPerformanceService {

    private final AgentPerformanceRepository agentPerformanceRepository;
    private final AgentTargetRepository agentTargetRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    @Override
    public List<AgentPerformanceResponseDTO> getAllAgentPerformance() {
        return agentPerformanceRepository.findAll().stream().map(AgentPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<AgentPerformanceResponseDTO> getPerformanceByMonth(Integer year, Integer month) {
        return agentPerformanceRepository.findByPerformanceYearAndPerformanceMonth(year, month).stream().map(AgentPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<AgentPerformanceResponseDTO> getPerformanceByAgent(Long agentId) {
        return agentPerformanceRepository.findByAgentId(agentId).stream().map(AgentPerformanceMapper::toResponseDTO).toList();
    }

    @Override
    public List<AgentPerformanceResponseDTO> generateMonthlyPerformance(Integer year, Integer month) {

        /*
         * We generate performance only for agents who have targets for the given month.
         * This avoids creating empty performance records for admin/supervisor users.
         */
        List<AgentTarget> monthlyTargets = agentTargetRepository.findByTargetYearAndTargetMonth(year, month);

        for (AgentTarget target : monthlyTargets) {

            User agent = target.getAgent();

            Long totalLeads = safeLong(leadRepository.countMonthlyLeadsByAgent(agent.getId(), year, month));

            Long convertedLeads = safeLong(leadRepository.countMonthlyConvertedLeadsByAgent(agent.getId(), year, month));

            Long cancelledLeads = safeLong(leadRepository.countMonthlyCancelledLeadsByAgent(agent.getId(), year, month));

            Long onHoldLeads = safeLong(leadRepository.countMonthlyOnHoldLeadsByAgent(agent.getId(), year, month));

            BigDecimal totalPremium = safeBigDecimal(leadRepository.sumMonthlyCompletedPremiumByAgent(agent.getId(), year, month));

            BigDecimal targetPremium = safeBigDecimal(target.getTargetPremium());

            BigDecimal conversionRate = calculatePercentage(BigDecimal.valueOf(convertedLeads), BigDecimal.valueOf(totalLeads));

            BigDecimal targetAchievementPercentage = calculatePercentage(totalPremium, targetPremium);

            BigDecimal averagePremium = convertedLeads > 0 ? totalPremium.divide(BigDecimal.valueOf(convertedLeads), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            BigDecimal performanceScore = calculatePerformanceScore(conversionRate, targetAchievementPercentage);

            AgentPerformance performance = agentPerformanceRepository.findByAgentAndPerformanceYearAndPerformanceMonth(agent, year, month).orElse(new AgentPerformance());

            performance.setAgent(agent);
            performance.setPerformanceYear(year);
            performance.setPerformanceMonth(month);

            performance.setTotalLeads(totalLeads.intValue());
            performance.setConvertedLeads(convertedLeads.intValue());
            performance.setCancelledLeads(cancelledLeads.intValue());
            performance.setOnHoldLeads(onHoldLeads.intValue());

            performance.setTotalPremium(totalPremium);
            performance.setTargetPremium(targetPremium);
            performance.setTargetAchievementPercentage(targetAchievementPercentage);

            performance.setConversionRate(conversionRate);
            performance.setAveragePremium(averagePremium);
            performance.setPerformanceScore(performanceScore);

            agentPerformanceRepository.save(performance);
        }

        return getPerformanceByMonth(year, month);
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (value == null || total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePerformanceScore(BigDecimal conversionRate, BigDecimal targetAchievementPercentage) {
        /*
         * Simple research-friendly scoring formula:
         *
         * 40% = conversion rate
         * 60% = target achievement
         */
        BigDecimal conversionPart = safeBigDecimal(conversionRate).multiply(BigDecimal.valueOf(0.40));

        BigDecimal targetPart = safeBigDecimal(targetAchievementPercentage).multiply(BigDecimal.valueOf(0.60));

        BigDecimal score = conversionPart.add(targetPart);

        /*
         * Cap score at 100 to keep dashboard display clean.
         */
        if (score.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP);
        }

        return score.setScale(2, RoundingMode.HALF_UP);
    }

    private Long safeLong(Long value) {
        return value != null ? value : 0L;
    }

    private BigDecimal safeBigDecimal(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
