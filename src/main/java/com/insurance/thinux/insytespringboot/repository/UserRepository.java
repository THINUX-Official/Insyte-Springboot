package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.dto.request.UserRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.UserResponseDTO;
import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author: THINUX
 * @created: 18-Feb-26 - 10:06 PM
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByStatus(UserStatus status);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}
