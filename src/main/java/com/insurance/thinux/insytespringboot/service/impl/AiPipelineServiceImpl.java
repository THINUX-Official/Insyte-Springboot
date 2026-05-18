package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.AiPipelineResponseDTO;
import com.insurance.thinux.insytespringboot.service.AiPipelineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class AiPipelineServiceImpl implements AiPipelineService {

    @Value("${app.ml.directory}")
    private String mlDirectory;

    @Value("${app.ml.python-command:python}")
    private String pythonCommand;

    private static final String PIPELINE_SCRIPT = "run_ai_pipeline.py";

    @Override
    public AiPipelineResponseDTO runAiPipeline() {

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        try {
            File workingDirectory = new File(mlDirectory);

            if (!workingDirectory.exists() || !workingDirectory.isDirectory()) {
                return new AiPipelineResponseDTO(false, -1, "ML directory not found", "", "Invalid ML directory: " + workingDirectory.getAbsolutePath());
            }

            File scriptFile = new File(workingDirectory, PIPELINE_SCRIPT);

            if (!scriptFile.exists() || !scriptFile.isFile()) {
                return new AiPipelineResponseDTO(false, -1, "Pipeline script not found", "", "Missing script: " + scriptFile.getAbsolutePath());
            }

            ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, PIPELINE_SCRIPT);

            processBuilder.directory(workingDirectory);
            processBuilder.redirectErrorStream(false);

            Process process = processBuilder.start();

            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)); BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;

                while ((line = outputReader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }

                while ((line = errorReader.readLine()) != null) {
                    error.append(line).append(System.lineSeparator());
                }
            }

            int exitCode = process.waitFor();
            boolean success = exitCode == 0;

            return new AiPipelineResponseDTO(success, exitCode, success ? "AI pipeline completed successfully" : "AI pipeline failed", output.toString(), error.toString());

        } catch (Exception e) {
            return new AiPipelineResponseDTO(false, -1, "Error while running AI pipeline", output.toString(), e.getMessage());
        }
    }
}