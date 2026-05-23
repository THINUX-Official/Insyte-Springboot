package com.insurance.thinux.insytespringboot.service;

import com.insurance.thinux.insytespringboot.dto.request.MlExperimentRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.MlExperimentResponseDTO;

import java.util.List;

public interface MlExperimentService {

    List<MlExperimentResponseDTO> getAllExperiments();

    MlExperimentResponseDTO createExperiment(MlExperimentRequestDTO requestDTO);
}
