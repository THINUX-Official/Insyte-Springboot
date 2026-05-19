package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.OccupationResponseDTO;
import com.insurance.thinux.insytespringboot.model.Occupation;
import com.insurance.thinux.insytespringboot.repository.OccupationRepository;
import com.insurance.thinux.insytespringboot.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupationServiceImpl implements OccupationService {

    private final OccupationRepository occupationRepository;

    @Override
    public List<OccupationResponseDTO> getAllOccupations() {
        return occupationRepository.findAllByOrderByNameAsc().stream().map(this::mapToResponse).toList();
    }

    private OccupationResponseDTO mapToResponse(Occupation occupation) {
        return new OccupationResponseDTO(occupation.getId(), occupation.getCode(), occupation.getName());
    }
}