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
public class DiseaseScore {
    private String diseaseName;
    private BigDecimal score;   // 0-100
    private Disease.Severity severity;
    private String description;
    private String advice;
}
