package com.diseasepredictor.dto;

import com.diseasepredictor.entity.PatientProfile;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientProfileUpdateRequest {
    private LocalDate dateOfBirth;
    private PatientProfile.Gender gender;
    private String bloodGroup;
    private String address;
    private String medicalHistory;
    private String allergies;
    private String prescription;
    private LocalDate nextVisitDate;
    private String fullName;
    private String phone;
}
