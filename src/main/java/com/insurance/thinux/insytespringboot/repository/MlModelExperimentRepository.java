package com.insurance.thinux.insytespringboot.repository;

import com.insurance.thinux.insytespringboot.enums.MlModelType;
import com.insurance.thinux.insytespringboot.model.MlModelExperiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MlModelExperimentRepository extends JpaRepository<MlModelExperiment, Long> {

    List<MlModelExperiment> findByModelType(MlModelType modelType);

    List<MlModelExperiment> findByAlgorithm(String algorithm);
}
