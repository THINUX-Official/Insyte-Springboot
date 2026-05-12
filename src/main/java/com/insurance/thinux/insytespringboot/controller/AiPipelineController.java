package com.insurance.thinux.insytespringboot.controller;

import com.insurance.thinux.insytespringboot.dto.response.AiPipelineResponseDTO;
import com.insurance.thinux.insytespringboot.service.AiPipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai-pipeline")
@RequiredArgsConstructor
public class AiPipelineController {

    private final AiPipelineService aiPipelineService;

    @PostMapping("/run")
    public AiPipelineResponseDTO runAiPipeline() {
        return aiPipelineService.runAiPipeline();
    }
}
