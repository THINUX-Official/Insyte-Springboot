package com.insurance.thinux.insytespringboot.mapper;

import com.insurance.thinux.insytespringboot.dto.request.MlExperimentRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.MlExperimentResponseDTO;
import com.insurance.thinux.insytespringboot.model.MlModelExperiment;

import java.time.LocalDateTime;

public class MlExperimentMapper {

    private MlExperimentMapper() {
        // Prevent object creation
    }

    public static MlExperimentResponseDTO toResponseDTO(MlModelExperiment experiment) {

        if (experiment == null) {
            return null;
        }

        MlExperimentResponseDTO dto = new MlExperimentResponseDTO();

        dto.setId(experiment.getId());
        dto.setModelName(experiment.getModelName());
        dto.setModelType(experiment.getModelType());
        dto.setAlgorithm(experiment.getAlgorithm());
        dto.setDatasetSize(experiment.getDatasetSize());

        dto.setAccuracyScore(experiment.getAccuracyScore());
        dto.setMae(experiment.getMae());
        dto.setRmse(experiment.getRmse());
        dto.setR2Score(experiment.getR2Score());
        dto.setPrecisionScore(experiment.getPrecisionScore());
        dto.setRecallScore(experiment.getRecallScore());
        dto.setF1Score(experiment.getF1Score());

        dto.setModelVersion(experiment.getModelVersion());
        dto.setRemarks(experiment.getRemarks());
        dto.setCreatedAt(experiment.getCreatedAt());

        return dto;
    }

    public static MlModelExperiment toEntity(MlExperimentRequestDTO requestDTO) {

        if (requestDTO == null) {
            return null;
        }

        MlModelExperiment experiment = new MlModelExperiment();

        experiment.setModelName(requestDTO.getModelName());
        experiment.setModelType(requestDTO.getModelType());
        experiment.setAlgorithm(requestDTO.getAlgorithm());
        experiment.setDatasetSize(requestDTO.getDatasetSize());

        experiment.setAccuracyScore(requestDTO.getAccuracyScore());
        experiment.setMae(requestDTO.getMae());
        experiment.setRmse(requestDTO.getRmse());
        experiment.setR2Score(requestDTO.getR2Score());
        experiment.setPrecisionScore(requestDTO.getPrecisionScore());
        experiment.setRecallScore(requestDTO.getRecallScore());
        experiment.setF1Score(requestDTO.getF1Score());

        experiment.setModelVersion(requestDTO.getModelVersion());
        experiment.setRemarks(requestDTO.getRemarks());
        experiment.setCreatedAt(LocalDateTime.now());

        return experiment;
    }
}
