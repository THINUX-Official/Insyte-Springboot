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

    boolean existsByNic(String nic);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    List<Lead> findAll();

    Optional<Lead> findByNic(String nic);

    @Query("""
            SELECT COUNT(l)
            FROM Lead l
            WHERE l.assignedUser.id = :agentId
            AND YEAR(l.createdAt) = :year
            AND MONTH(l.createdAt) = :month
            """)
    Long countMonthlyLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query("""
            SELECT COUNT(l)
            FROM Lead l
            WHERE l.assignedUser.id = :agentId
            AND l.status = 'COMPLETED'
            AND YEAR(l.createdAt) = :year
            AND MONTH(l.createdAt) = :month
            """)
    Long countMonthlyConvertedLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query("""
            SELECT COUNT(l)
            FROM Lead l
            WHERE l.assignedUser.id = :agentId
            AND l.status = 'CANCELLED'
            AND YEAR(l.createdAt) = :year
            AND MONTH(l.createdAt) = :month
            """)
    Long countMonthlyCancelledLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query("""
            SELECT COUNT(l)
            FROM Lead l
            WHERE l.assignedUser.id = :agentId
            AND l.status = 'ON_HOLD'
            AND YEAR(l.createdAt) = :year
            AND MONTH(l.createdAt) = :month
            """)
    Long countMonthlyOnHoldLeadsByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);

    @Query("""
            SELECT COALESCE(SUM(l.premium), 0)
            FROM Lead l
            WHERE l.assignedUser.id = :agentId
            AND l.status = 'COMPLETED'
            AND YEAR(l.createdAt) = :year
            AND MONTH(l.createdAt) = :month
            """)
    BigDecimal sumMonthlyCompletedPremiumByAgent(@Param("agentId") Long agentId, @Param("year") Integer year, @Param("month") Integer month);
}
