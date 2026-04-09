package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
