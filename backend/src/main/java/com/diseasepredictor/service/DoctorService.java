package com.diseasepredictor.service;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.entity.*;
import com.diseasepredictor.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired private UserRepo userRepo;
    @Autowired private DoctorProfileRepo doctorProfileRepo;
    @Autowired private PatientProfileRepo patientProfileRepo;
    @Autowired private PredictionRepo predictionRepo;

    @Transactional(readOnly = true)
    public DoctorProfileDto getProfile(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        DoctorProfile profile = doctorProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));

        DoctorProfileDto dto = new DoctorProfileDto();
        dto.setId(profile.getId());
        dto.setUserId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setSpecialization(profile.getSpecialization());
        dto.setQualification(profile.getQualification());
        dto.setExperienceYrs(profile.getExperienceYrs());
        dto.setLicenseNumber(profile.getLicenseNumber());
        dto.setHospital(profile.getHospital());
        long total = userRepo.findByRole(User.Role.PATIENT).size();
        dto.setTotalPatients(total);
        return dto;
    }

    @Transactional
    public DoctorProfileDto updateProfile(String username, DoctorProfileUpdateRequest req) {
        User user = userRepo.findByUsername(username).orElseThrow();
        DoctorProfile profile = doctorProfileRepo.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor profile not found"));

        if (req.getSpecialization() != null) profile.setSpecialization(req.getSpecialization());
        if (req.getQualification() != null) profile.setQualification(req.getQualification());
        if (req.getExperienceYrs() != null) profile.setExperienceYrs(req.getExperienceYrs());
        if (req.getLicenseNumber() != null) profile.setLicenseNumber(req.getLicenseNumber());
        if (req.getHospital() != null) profile.setHospital(req.getHospital());
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());

        userRepo.save(user);
        doctorProfileRepo.save(profile);
        return getProfile(username);
    }

    @Transactional(readOnly = true)
    public PatientProfileDto getPatientById(Long patientId) {
        User user = userRepo.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (user.getRole() != User.Role.PATIENT) {
            throw new IllegalArgumentException("User is not a patient");
        }
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
    public PatientProfileDto updatePatientById(Long patientId, PatientProfileUpdateRequest req) {
        User user = userRepo.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));
        if (user.getRole() != User.Role.PATIENT) {
            throw new IllegalArgumentException("Cannot modify non-patient user");
        }

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
        return getPatientById(patientId);
    }

    @Transactional(readOnly = true)
    public List<PatientProfileDto> getAllPatients() {
        return userRepo.findByRole(User.Role.PATIENT).stream().map(u -> {
            PatientProfileDto dto = new PatientProfileDto();
            dto.setUserId(u.getId());
            dto.setFullName(u.getFullName());
            dto.setEmail(u.getEmail());
            dto.setPhone(u.getPhone());
            dto.setTotalPredictions(predictionRepo.countByPatientId(u.getId()));
            return dto;
        }).collect(Collectors.toList());
    }
}
