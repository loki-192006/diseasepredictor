package com.diseasepredictor.dto;

import lombok.Data;

@Data
public class DoctorProfileUpdateRequest {
    private String specialization;
    private String qualification;
    private Integer experienceYrs;
    private String licenseNumber;
    private String hospital;
    private String fullName;
    private String phone;
}
