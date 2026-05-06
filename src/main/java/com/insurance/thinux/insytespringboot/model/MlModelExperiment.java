package com.insurance.thinux.insytespringboot.model;

import com.insurance.thinux.insytespringboot.enums.MlModelType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ml_model_experiments")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MlModelExperiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "model_type", nullable = false, length = 50)
    private MlModelType modelType;

    @Column(nullable = false, length = 100)
    private String algorithm;

    @Column(name = "dataset_size")
    private Integer datasetSize;

    @Column(name = "accuracy_score", precision = 8, scale = 4)
    private BigDecimal accuracyScore;

    @Column(precision = 10, scale = 4)
    private BigDecimal mae;

    @Column(precision = 10, scale = 4)
    private BigDecimal rmse;

    @Column(name = "r2_score", precision = 10, scale = 4)
    private BigDecimal r2Score;

    @Column(name = "precision_score", precision = 8, scale = 4)
    private BigDecimal precisionScore;

    @Column(name = "recall_score", precision = 8, scale = 4)
    private BigDecimal recallScore;

    @Column(name = "f1_score", precision = 8, scale = 4)
    private BigDecimal f1Score;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
