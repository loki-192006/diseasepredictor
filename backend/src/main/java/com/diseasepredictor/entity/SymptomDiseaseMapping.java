package com.diseasepredictor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "symptom_disease_mapping")
@Data
@NoArgsConstructor
public class SymptomDiseaseMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disease_id", nullable = false)
    private Disease disease;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symptom_id", nullable = false)
    private Symptom symptom;

    @Column(precision = 3, scale = 2)
    private BigDecimal weight = BigDecimal.ONE;
}
