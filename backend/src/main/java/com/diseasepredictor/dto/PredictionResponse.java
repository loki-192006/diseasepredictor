package com.diseasepredictor.dto;

import com.diseasepredictor.entity.Disease;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponse {
    private Long predictionId;
    private String predictedDisease;
    private BigDecimal confidenceScore;
    private Disease.Severity severity;
    private String advice;
    private List<String> symptomsProvided;
    private List<DiseaseScore> allResults;
    private LocalDateTime predictedAt;
}
