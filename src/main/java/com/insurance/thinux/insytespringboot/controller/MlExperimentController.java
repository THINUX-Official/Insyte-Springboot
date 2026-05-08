package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.request.MlExperimentRequestDTO;
import com.insurance.thinux.insytespringboot.dto.response.MlExperimentResponseDTO;
import com.insurance.thinux.insytespringboot.service.MlExperimentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ml-experiments")
@RequiredArgsConstructor
public class MlExperimentController {

    private final MlExperimentService mlExperimentService;

    @GetMapping
    public List<MlExperimentResponseDTO> getAllExperiments() {
        return mlExperimentService.getAllExperiments();
    }

    @PostMapping
    public MlExperimentResponseDTO createExperiment(@RequestBody MlExperimentRequestDTO requestDTO) {
        return mlExperimentService.createExperiment(requestDTO);
    }
}
