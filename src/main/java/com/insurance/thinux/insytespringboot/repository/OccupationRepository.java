package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {

    List<Occupation> findAllByOrderByNameAsc();
}