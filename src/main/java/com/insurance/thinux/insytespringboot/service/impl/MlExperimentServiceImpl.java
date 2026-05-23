package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.request.MlExperimentRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.MlExperimentResponseDTO;
import com.insurance.thinux.insytespringboot.mapper.MlExperimentMapper;
import com.insurance.thinux.insytespringboot.model.MlModelExperiment;
import com.insurance.thinux.insytespringboot.repository.MlModelExperimentRepository;
import com.insurance.thinux.insytespringboot.service.MlExperimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MlExperimentServiceImpl implements MlExperimentService {

    private final MlModelExperimentRepository mlModelExperimentRepository;

    @Override
    public List<MlExperimentResponseDTO> getAllExperiments() {
        return mlModelExperimentRepository.findAll().stream().map(MlExperimentMapper::toResponseDTO).toList();
    }

    @Override
    public MlExperimentResponseDTO createExperiment(MlExperimentRequestDTO requestDTO) {

        MlModelExperiment experiment = MlExperimentMapper.toEntity(requestDTO);

        MlModelExperiment savedExperiment = mlModelExperimentRepository.save(experiment);

        return MlExperimentMapper.toResponseDTO(savedExperiment);
    }

}
