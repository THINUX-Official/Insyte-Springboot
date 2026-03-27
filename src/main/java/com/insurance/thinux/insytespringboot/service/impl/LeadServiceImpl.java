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
import com.insurance.thinux.insytespringboot.service.LeadService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadServiceImpl implements LeadService {
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final OccupationRepository occupationRepository;
    private final LeadMapper leadMapper;

    public LeadServiceImpl(LeadRepository leadRepository, UserRepository userRepository, OccupationRepository occupationRepository, LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.occupationRepository = occupationRepository;
        this.leadMapper = leadMapper;
    }


    @Override
    public List<LeadResponseDTO> getAllLeads() {
        return leadRepository.findAll()
                .stream()
                .map(leadMapper::toDTO)
                .toList();
    }

    @Override
    public LeadResponseDTO createLead(LeadRequestDTO dto) {

        if (leadRepository.existsByNic(dto.getNic())) {
            throw new RuntimeException("NIC already exists");
        }

        if (leadRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email address already exists");
        }

        if (leadRepository.existsByMobile(dto.getMobile())) {
            throw new RuntimeException("Mobile number already exists");
        }

        User user = userRepository.findById(dto.getAssignedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Occupation occupation = null;
        if (dto.getOccupationId() != null) {
            occupation = occupationRepository.findById(dto.getOccupationId())
                    .orElseThrow(() -> new RuntimeException("Occupation not found"));
        }

        Lead lead = leadMapper.toEntity(dto, user, occupation);

        return leadMapper.toDTO(leadRepository.save(lead));
    }

    @Override
    public LeadResponseDTO getLeadById(Long id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return leadMapper.toDTO(lead);
    }

    @Override
    public LeadResponseDTO updateLead(Long id, LeadRequestDTO dto) {

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        User user = userRepository.findById(dto.getAssignedUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Occupation occupation = null;
        if (dto.getOccupationId() != null) {
            occupation = occupationRepository.findById(dto.getOccupationId())
                    .orElseThrow(() -> new RuntimeException("Occupation not found"));
        }

        leadMapper.updateEntity(lead, dto, user, occupation);
        return leadMapper.toDTO(leadRepository.save(lead));
    }

    @Override
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new RuntimeException("Lead not found");
        }
        leadRepository.deleteById(id);
    }
}
