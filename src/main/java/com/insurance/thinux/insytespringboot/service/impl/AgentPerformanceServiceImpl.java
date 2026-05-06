package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.model.AgentPerformance;
import com.insurance.thinux.insytespringboot.model.AgentTarget;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.AgentPerformanceRepository;
import com.insurance.thinux.insytespringboot.repository.AgentTargetRepository;
import com.insurance.thinux.insytespringboot.repository.LeadRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AgentPerformanceServiceImpl implements AgentPerformanceService {

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final AgentTargetRepository agentTargetRepository;
    private final AgentPerformanceRepository agentPerformanceRepository;

    @Override
    public void generateMonthlyPerformance(Integer year, Integer month) {
        List<User> users = userRepository.findAll();

        for (User agent : users) {

            Long totalLeads = leadRepository.countMonthlyLeadsByAgent(agent.getId(), year, month);
            Long convertedLeads = leadRepository.countMonthlyConvertedLeadsByAgent(agent.getId(), year, month);
            Long cancelledLeads = leadRepository.countMonthlyCancelledLeadsByAgent(agent.getId(), year, month);
            Long onHoldLeads = leadRepository.countMonthlyOnHoldLeadsByAgent(agent.getId(), year, month);
            BigDecimal totalPremium = leadRepository.sumMonthlyCompletedPremiumByAgent(agent.getId(), year, month);

            if (totalPremium == null) {
                totalPremium = BigDecimal.ZERO;
            }

            AgentTarget target = agentTargetRepository.findByAgentAndTargetYearAndTargetMonth(agent, year, month).orElse(null);

            BigDecimal targetPremium = target != null && target.getTargetPremium() != null ? target.getTargetPremium() : BigDecimal.ZERO;

            BigDecimal conversionRate = calculatePercentage(BigDecimal.valueOf(convertedLeads), BigDecimal.valueOf(totalLeads));

            BigDecimal targetAchievement = calculatePercentage(totalPremium, targetPremium);

            BigDecimal averagePremium = totalLeads > 0 ? totalPremium.divide(BigDecimal.valueOf(totalLeads), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

            BigDecimal performanceScore = calculatePerformanceScore(conversionRate, targetAchievement);

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
            performance.setConversionRate(conversionRate);
            performance.setTargetAchievementPercentage(targetAchievement);
            performance.setAveragePremium(averagePremium);
            performance.setPerformanceScore(performanceScore);

            agentPerformanceRepository.save(performance);
        }
    }

    private BigDecimal calculatePercentage(BigDecimal value, BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return value.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePerformanceScore(BigDecimal conversionRate, BigDecimal targetAchievement) {
        BigDecimal conversionPart = conversionRate.multiply(BigDecimal.valueOf(0.4));
        BigDecimal targetPart = targetAchievement.multiply(BigDecimal.valueOf(0.6));

        BigDecimal score = conversionPart.add(targetPart);

        if (score.compareTo(BigDecimal.valueOf(100)) > 0) {
            return BigDecimal.valueOf(100);
        }

        return score.setScale(2, RoundingMode.HALF_UP);
    }
}
