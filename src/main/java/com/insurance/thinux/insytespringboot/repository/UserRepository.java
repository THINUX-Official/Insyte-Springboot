package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author: THINUX
 * @created: 18-Feb-26 - 10:06 PM
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> deleteUserByUsername(String username);
}
