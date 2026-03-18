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
public class PredictionHistoryItem {
    private Long id;
    private String predictedDisease;
    private BigDecimal confidenceScore;
    private Disease.Severity severity;
    private List<String> symptomsProvided;
    private LocalDateTime predictedAt;
}
