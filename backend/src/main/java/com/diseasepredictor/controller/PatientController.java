package com.diseasepredictor.controller;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/patient")
@CrossOrigin
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    @Autowired private PatientService patientService;
    @Autowired private PredictionService predictionService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<PatientProfileDto>> getProfile(Principal principal) {
        return ResponseEntity.ok(ApiResponse.ok(patientService.getProfile(principal.getName())));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<PatientProfileDto>> updateProfile(
            Principal principal,
            @RequestBody PatientProfileUpdateRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Profile updated",
                patientService.updateProfile(principal.getName(), req)));
    }

    @PostMapping("/predict")
    public ResponseEntity<ApiResponse<PredictionResponse>> predict(
            Principal principal,
            @Valid @RequestBody PredictionRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Prediction complete",
                predictionService.predict(principal.getName(), req)));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<PredictionHistoryItem>>> history(Principal principal) {
        return ResponseEntity.ok(ApiResponse.ok(predictionService.getHistory(principal.getName())));
    }
}
