package com.insurance.thinux.insytespringboot.dto.request;

import com.insurance.thinux.insytespringboot.enums.MlModelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MlExperimentRequestDTO {

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

}
