package com.diseasepredictor.dto;

import lombok.Data;

@Data
public class DoctorProfileDto {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
    private String qualification;
    private Integer experienceYrs;
    private String licenseNumber;
    private String hospital;
    private long totalPatients;
}
