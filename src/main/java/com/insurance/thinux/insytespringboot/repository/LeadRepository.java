package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    boolean existsByNic(String nic);

    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    List<Lead> findAll();

    Optional<Lead> findByNic(String nic);
}
