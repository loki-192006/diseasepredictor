package com.diseasepredictor.controller;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.service.PredictionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/symptoms")
@CrossOrigin
public class SymptomController {

    @Autowired private PredictionService predictionService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SymptomDto>>> getAllSymptoms() {
        return ResponseEntity.ok(ApiResponse.ok(predictionService.getAllSymptoms()));
    }
}
