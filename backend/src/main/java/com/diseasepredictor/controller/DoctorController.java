package com.diseasepredictor.controller;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    @Autowired private DoctorService doctorService;
    @Autowired private PredictionService predictionService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileDto>> getProfile(Principal principal) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getProfile(principal.getName())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<DoctorProfileDto>> updateProfile(
            Principal principal,
            @RequestBody DoctorProfileUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated",
                doctorService.updateProfile(principal.getName(), req)));
    }

    @GetMapping("/patients")
    public ResponseEntity<ApiResponse<List<PatientProfileDto>>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getAllPatients()));
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<ApiResponse<PatientProfileDto>> getPatientById(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.ok(doctorService.getPatientById(patientId)));
    }

    @PutMapping("/patients/{patientId}")
    public ResponseEntity<ApiResponse<PatientProfileDto>> updatePatientById(
            @PathVariable Long patientId,
            @RequestBody PatientProfileUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Patient details updated", doctorService.updatePatientById(patientId, req)));
    }

    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<ApiResponse<List<PredictionHistoryItem>>> patientHistory(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.ok(predictionService.getPatientHistory(patientId)));
    }
}
