package com.diseasepredictor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "diseases")
@Data
@NoArgsConstructor
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Severity severity = Severity.MEDIUM;

    @Column(columnDefinition = "TEXT")
    private String advice;

    @OneToMany(mappedBy = "disease", fetch = FetchType.LAZY)
    private List<SymptomDiseaseMapping> symptomMappings;

    public enum Severity { LOW, MEDIUM, HIGH, CRITICAL }
}
