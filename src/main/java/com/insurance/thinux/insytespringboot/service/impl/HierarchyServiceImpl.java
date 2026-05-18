package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.enums.UserStatus;
import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.Role;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.LeadRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.security.JwtService;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class HierarchyServiceImpl implements HierarchyService {

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final JwtService jwtService;
    private final HttpServletRequest request;

    @Override
    public User getCurrentUser() {
        String username = getUsernameFromRequest();

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Current user not found. Send Authorization: Bearer <token> or X-Username header.");
        }

        return userRepository.findByUsernameAndStatus(username, UserStatus.ACTIVE).orElseThrow(() -> new RuntimeException("Active user not found for username: " + username));
    }

    @Override
    public List<Long> getVisibleUserIdsForCurrentUser() {
        User currentUser = getCurrentUser();

        if (hasRole(currentUser, "ADMIN")) {
            return userRepository.findAllByStatusOrderByIdDesc(UserStatus.ACTIVE).stream().map(User::getId).toList();
        }

        Set<Long> visibleIds = new HashSet<>();
        visibleIds.add(currentUser.getId());
        collectSubordinateIds(currentUser.getId(), visibleIds);

        return new ArrayList<>(visibleIds);
    }

    @Override
    public List<Long> getVisibleIcIdsForCurrentUser() {
        User currentUser = getCurrentUser();

        if (hasRole(currentUser, "ADMIN")) {
            return userRepository.findActiveUsersByRoleName("IC").stream().map(User::getId).toList();
        }

        if (hasRole(currentUser, "IC")) {
            return List.of(currentUser.getId());
        }

        List<Long> visibleUserIds = getVisibleUserIdsForCurrentUser();

        if (visibleUserIds.isEmpty()) {
            return List.of();
        }

        return userRepository.findActiveUsersByIdsAndRoleName(visibleUserIds, "IC").stream().map(User::getId).toList();
    }

    @Override
    public boolean canAccessUser(Long userId) {
        if (userId == null) {
            return false;
        }

        return getVisibleUserIdsForCurrentUser().contains(userId);
    }

    @Override
    public boolean canAccessLead(Long leadId) {
        if (leadId == null) {
            return false;
        }

        Lead lead = leadRepository.findById(leadId).orElseThrow(() -> new RuntimeException("Lead not found"));

        return lead.getAssignedUser() != null && canAccessUser(lead.getAssignedUser().getId());
    }

    @Override
    public boolean isCurrentUserAdmin() {
        User currentUser = getCurrentUser();
        return hasRole(currentUser, "ADMIN");
    }

    private void collectSubordinateIds(Long supervisorId, Set<Long> visibleIds) {
        List<User> directSubordinates = userRepository.findBySupervisorIdAndStatus(supervisorId, UserStatus.ACTIVE);

        for (User subordinate : directSubordinates) {
            if (visibleIds.add(subordinate.getId())) {
                collectSubordinateIds(subordinate.getId(), visibleIds);
            }
        }
    }

    private String getUsernameFromRequest() {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtService.isTokenValid(token)) {
                return jwtService.extractUsername(token);
            }
        }

        return request.getHeader("X-Username");
    }

    private boolean hasRole(User user, String roleName) {
        if (user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream().map(Role::getName).map(this::normalizeRole).anyMatch(role -> role.equals(normalizeRole(roleName)));
    }

    private String normalizeRole(String roleName) {
        if (roleName == null) {
            return "";
        }

        return roleName.trim().toUpperCase().replace("ROLE_", "");
    }
}