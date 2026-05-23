package com.insurance.thinux.insytespringboot.dto.response;

import com.insurance.thinux.insytespringboot.enums.MlModelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MlExperimentResponseDTO {

    private Long id;

    private String modelName;
    private MlModelType modelType;
    private String algorithm;

    private Integer datasetSize;

    private BigDecimal accuracyScore;
    private BigDecimal mae;
    private BigDecimal rmse;
    private BigDecimal r2Score;
    private BigDecimal precisionScore;
    private BigDecimal recallScore;
    private BigDecimal f1Score;

    private String modelVersion;
    private String remarks;

    private LocalDateTime createdAt;
}
