package com.diseasepredictor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "predictions")
@Data
@NoArgsConstructor
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    @Column(name = "predicted_disease", nullable = false, length = 150)
    private String predictedDisease;

    @Column(name = "confidence_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "symptoms_provided", nullable = false, columnDefinition = "TEXT")
    private String symptomsProvided;   // JSON

    @Column(name = "all_results", columnDefinition = "TEXT")
    private String allResults;          // JSON

    @Enumerated(EnumType.STRING)
    private Disease.Severity severity;

    @Column(columnDefinition = "TEXT")
    private String advice;

    @Column(name = "predicted_at", updatable = false)
    private LocalDateTime predictedAt = LocalDateTime.now();
}
