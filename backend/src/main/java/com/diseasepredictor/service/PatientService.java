package com.diseasepredictor.service;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.entity.*;
import com.diseasepredictor.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientService {

    @Autowired private UserRepo userRepo;
    @Autowired private PatientProfileRepo patientProfileRepo;
    @Autowired private PredictionRepo predictionRepo;

    @Transactional(readOnly = true)
    public PatientProfileDto getProfile(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        PatientProfile profile = patientProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found"));

        PatientProfileDto dto = new PatientProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(profile.getDateOfBirth());
        dto.setGender(profile.getGender());
        dto.setBloodGroup(profile.getBloodGroup());
        dto.setAddress(profile.getAddress());
        dto.setMedicalHistory(profile.getMedicalHistory());
        dto.setAllergies(profile.getAllergies());
        dto.setPrescription(profile.getPrescription());
        dto.setNextVisitDate(profile.getNextVisitDate());
        dto.setTotalPredictions(predictionRepo.countByPatientId(user.getId()));
        return dto;
    }

    @Transactional
    public PatientProfileDto updateProfile(String username, PatientProfileUpdateRequest req) {
        User user = userRepo.findByUsername(username).orElseThrow();
        PatientProfile profile = patientProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient profile not found"));

        if (req.getDateOfBirth() != null) profile.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender() != null) profile.setGender(req.getGender());
        if (req.getBloodGroup() != null) profile.setBloodGroup(req.getBloodGroup());
        if (req.getAddress() != null) profile.setAddress(req.getAddress());
        if (req.getMedicalHistory() != null) profile.setMedicalHistory(req.getMedicalHistory());
        if (req.getAllergies() != null) profile.setAllergies(req.getAllergies());
        if (req.getPrescription() != null) profile.setPrescription(req.getPrescription());
        if (req.getNextVisitDate() != null) profile.setNextVisitDate(req.getNextVisitDate());
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        userRepo.save(user);
        patientProfileRepo.save(profile);
        return getProfile(username);
    }
}
