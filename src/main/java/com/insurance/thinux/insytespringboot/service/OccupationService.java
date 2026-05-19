package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.response.OccupationResponseDTO;

import java.util.List;

public interface OccupationService {

    List<OccupationResponseDTO> getAllOccupations();
}