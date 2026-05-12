package com.insurance.thinux.insytespringboot.service.impl;

import com.insurance.thinux.insytespringboot.dto.response.AiPipelineResponseDTO;
import com.insurance.thinux.insytespringboot.service.AiPipelineService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Service
public class AiPipelineServiceImpl implements AiPipelineService {

    private static final String ML_DIRECTORY = "D:\\ESOFT\\PROJECT\\insyte\\Insyte-Springboot\\src\\main\\java\\com\\insurance\\thinux\\insytespringboot\\ml\\";

    private static final String PYTHON_COMMAND = "python";

    private static final String PIPELINE_SCRIPT = "run_ai_pipeline.py";

    @Override
    public AiPipelineResponseDTO runAiPipeline() {

        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(PYTHON_COMMAND, PIPELINE_SCRIPT);

            processBuilder.directory(new File(ML_DIRECTORY));
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
