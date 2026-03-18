package com.diseasepredictor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymptomDto {
    private Long id;
    private String name;
    private String category;
    private String description;
}
