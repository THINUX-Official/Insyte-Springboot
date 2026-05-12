package com.insurance.thinux.insytespringboot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AiPipelineResponseDTO {
    private boolean success;
    private int exitCode;
    private String message;
    private String output;
    private String error;
}
