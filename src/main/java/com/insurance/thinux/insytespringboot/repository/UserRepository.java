package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByStatusOrderByIdDesc(UserStatus status);

    Optional<User> findByUsernameAndStatus(String username, UserStatus status);

    List<User> findBySupervisorIdAndStatus(Long supervisorId, UserStatus status);

    List<User> findByIdInAndStatusOrderByIdDesc(List<Long> ids, UserStatus status);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN u.roles r
            WHERE u.status = com.insurance.thinux.insytespringboot.enums.UserStatus.ACTIVE
            AND UPPER(REPLACE(r.name, 'ROLE_', '')) = UPPER(:roleName)
            ORDER BY u.id DESC
            """)
    List<User> findActiveUsersByRoleName(@Param("roleName") String roleName);

    @Query("""
            SELECT DISTINCT u
            FROM User u
            JOIN u.roles r
            WHERE u.id IN :userIds
            AND u.status = com.insurance.thinux.insytespringboot.enums.UserStatus.ACTIVE
            AND UPPER(REPLACE(r.name, 'ROLE_', '')) = UPPER(:roleName)
            ORDER BY u.id DESC
            """)
    List<User> findActiveUsersByIdsAndRoleName(@Param("userIds") List<Long> userIds, @Param("roleName") String roleName);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}