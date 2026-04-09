package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.LeadRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.LeadResponseDTO;

import java.util.List;

public interface LeadService {
    LeadResponseDTO createLead(LeadRequestDTO dto);

    LeadResponseDTO getLeadById(Long id);

    List<LeadResponseDTO> getAllLeads();

    LeadResponseDTO updateLead(Long id, LeadRequestDTO dto);

    void deleteLead(Long id);
}
