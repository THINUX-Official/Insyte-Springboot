package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.model.User;

import java.util.List;

public interface HierarchyService {

    User getCurrentUser();

    List<Long> getVisibleUserIdsForCurrentUser();

    List<Long> getVisibleIcIdsForCurrentUser();

    boolean canAccessUser(Long userId);

    boolean canAccessLead(Long leadId);

    boolean isCurrentUserAdmin();
}