package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findAll();

    Optional<Lead> findByNic(String nic);

    @Query(value = """
            SELECT COUNT(*)
            FROM leads l
            WHERE l.assigned_user_id = :agentId
            AND YEAR(l.created_at) = :year
            AND MONTH(l.created_at) = :month
            """, nativeQuery = true)
    Long countMonthlyLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT COUNT(*)
            FROM leads l
            WHERE l.assigned_user_id = :agentId
            AND l.status = 'COMPLETED'
            AND YEAR(l.created_at) = :year
            AND MONTH(l.created_at) = :month
            """, nativeQuery = true)
    Long countMonthlyConvertedLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT COUNT(*)
            FROM leads l
            WHERE l.assigned_user_id = :agentId
            AND l.status = 'CANCELLED'
            AND YEAR(l.created_at) = :year
            AND MONTH(l.created_at) = :month
            """, nativeQuery = true)
    Long countMonthlyCancelledLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT COUNT(*)
            FROM leads l
            WHERE l.assigned_user_id = :agentId
            AND l.status = 'ON_HOLD'
            AND YEAR(l.created_at) = :year
            AND MONTH(l.created_at) = :month
            """, nativeQuery = true)
    Long countMonthlyOnHoldLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query(value = """
            SELECT COALESCE(SUM(l.premium), 0)
            FROM leads l
            WHERE l.assigned_user_id = :agentId
            AND l.status = 'COMPLETED'
            AND YEAR(l.created_at) = :year
            AND MONTH(l.created_at) = :month
            """, nativeQuery = true)
    BigDecimal sumMonthlyCompletedPremiumByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);
}
