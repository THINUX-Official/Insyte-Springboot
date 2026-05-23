package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.LeadRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LeadResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.LeadMapper;
import com.insurance.thinux.insytespringboot.model.Lead;
import com.insurance.thinux.insytespringboot.model.Occupation;
import com.insurance.thinux.insytespringboot.model.User;
import com.insurance.thinux.insytespringboot.repository.LeadRepository;
import com.insurance.thinux.insytespringboot.repository.OccupationRepository;
import com.insurance.thinux.insytespringboot.repository.UserRepository;
import com.insurance.thinux.insytespringboot.service.HierarchyService;
import com.insurance.thinux.insytespringboot.service.LeadService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final OccupationRepository occupationRepository;
    private final LeadMapper leadMapper;
    private final HierarchyService hierarchyService;

    public LeadServiceImpl(LeadRepository leadRepository, UserRepository userRepository, OccupationRepository occupationRepository, LeadMapper leadMapper, HierarchyService hierarchyService) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.occupationRepository = occupationRepository;
        this.leadMapper = leadMapper;
        this.hierarchyService = hierarchyService;
    }

    @Override
    public List<LeadResponseDTO> getAllLeads() {
        return leadRepository.findAllByOrderByIdDesc().stream().map(leadMapper::toDTO).toList();
    }

    @Override
    public List<LeadResponseDTO> getMyTeamLeads() {

        if (hierarchyService.isCurrentUserAdmin()) {
            return leadRepository.findAllByOrderByIdDesc().stream().map(leadMapper::toDTO).toList();
        }

        List<Long> visibleUserIds = hierarchyService.getVisibleUserIdsForCurrentUser();

        if (visibleUserIds.isEmpty()) {
            return List.of();
        }

        return leadRepository.findByAssignedUserIdInOrderByIdDesc(visibleUserIds).stream().map(leadMapper::toDTO).toList();
    }

    @Override
    public LeadResponseDTO createLead(LeadRequestDTO dto) {
        User user = userRepository.findById(dto.getAssignedUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (!hierarchyService.canAccessUser(user.getId())) {
            throw new RuntimeException("You cannot assign lead to this user");
        }

        Occupation occupation = null;

        if (dto.getOccupationId() != null) {
            occupation = occupationRepository.findById(dto.getOccupationId()).orElseThrow(() -> new RuntimeException("Occupation not found"));
        }

        Lead lead = leadMapper.toEntity(dto, user, occupation);

        return leadMapper.toDTO(leadRepository.save(lead));
    }

    @Override
    public LeadResponseDTO getLeadById(Long id) {
        if (!hierarchyService.canAccessLead(id)) {
            throw new RuntimeException("You cannot access this lead");
        }

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));

        return leadMapper.toDTO(lead);
    }

    @Override
    public LeadResponseDTO updateLead(Long id, LeadRequestDTO dto) {
        if (!hierarchyService.canAccessLead(id)) {
            throw new RuntimeException("You cannot update this lead");
        }

        Lead lead = leadRepository.findById(id).orElseThrow(() -> new RuntimeException("Lead not found"));

        User user = userRepository.findById(dto.getAssignedUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (!hierarchyService.canAccessUser(user.getId())) {
            throw new RuntimeException("You cannot assign lead to this user");
        }

        Occupation occupation = null;

        if (dto.getOccupationId() != null) {
            occupation = occupationRepository.findById(dto.getOccupationId()).orElseThrow(() -> new RuntimeException("Occupation not found"));
        }

        leadMapper.updateEntity(lead, dto, user, occupation);

        return leadMapper.toDTO(leadRepository.save(lead));
    }

    @Override
    public void deleteLead(Long id) {
        if (!hierarchyService.canAccessLead(id)) {
            throw new RuntimeException("You cannot delete this lead");
        }

        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found");
        }

        leadRepository.deleteById(id);
    }
}