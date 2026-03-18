package com.diseasepredictor.service;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.entity.*;
import com.diseasepredictor.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    @Autowired private SymptomRepo symptomRepo;
    @Autowired private SymptomDiseaseMappingRepo mappingRepo;
    @Autowired private DiseaseRepo diseaseRepo;
    @Autowired private UserRepo userRepo;
    @Autowired private PredictionRepo predictionRepo;
    @Autowired private ObjectMapper objectMapper;

    // ── Fetch all symptoms ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<SymptomDto> getAllSymptoms() {
        return symptomRepo.findAllByOrderByCategoryAscNameAsc().stream()
                .map(s -> new SymptomDto(s.getId(), s.getName(), s.getCategory(), s.getDescription()))
                .collect(Collectors.toList());
    }

    // ── Run the prediction engine ─────────────────────────────
    @Transactional
    public PredictionResponse predict(String username, PredictionRequest request) {
        List<String> inputSymptomNames = request.getSymptoms();

        // Resolve symptom entities
        List<Symptom> symptoms = symptomRepo.findByNameIn(inputSymptomNames);
        if (symptoms.isEmpty())
            throw new IllegalArgumentException("No recognised symptoms provided.");

        List<Long> symptomIds = symptoms.stream().map(Symptom::getId).collect(Collectors.toList());

        // Fetch all mappings that touch any of the provided symptoms
        List<SymptomDiseaseMapping> mappings = mappingRepo.findBySymptomIdIn(symptomIds);

        // --- Score each disease ---
        // score = sum(weight of matched symptoms) / sum(weight of ALL symptoms for that disease)
        Map<Long, Disease>         diseaseMap       = new HashMap<>();
        Map<Long, Double>          matchedWeightMap = new HashMap<>();
        Map<Long, Double>          totalWeightMap   = new HashMap<>();

        for (SymptomDiseaseMapping m : mappings) {
            Disease d  = m.getDisease();
            long    dId = d.getId();
            diseaseMap.put(dId, d);
            matchedWeightMap.merge(dId, m.getWeight().doubleValue(), Double::sum);
        }

        // Get total weights per disease (need full mappings per disease)
        for (Long dId : diseaseMap.keySet()) {
            List<SymptomDiseaseMapping> allMappings = mappingRepo.findByDiseaseId(dId);
            double total = allMappings.stream()
                    .mapToDouble(m -> m.getWeight().doubleValue()).sum();
            totalWeightMap.put(dId, total);
        }

        // Normalise → confidence score 0-100
        List<DiseaseScore> scores = diseaseMap.entrySet().stream().map(entry -> {
            Long    dId     = entry.getKey();
            Disease disease = entry.getValue();
            double matched  = matchedWeightMap.getOrDefault(dId, 0.0);
            double total    = totalWeightMap.getOrDefault(dId, 1.0);
            double raw      = (matched / total) * 100.0;
            // Penalise if patient provided very few symptoms relative to disease mapping count
            int    providedCount = symptoms.size();
            double penalty       = Math.min(1.0, providedCount / (double) Math.max(1, (int)(total)));
            double finalScore    = raw * (0.6 + 0.4 * penalty);
            BigDecimal score     = BigDecimal.valueOf(finalScore).setScale(2, RoundingMode.HALF_UP);

            return new DiseaseScore(disease.getName(), score,
                    disease.getSeverity(), disease.getDescription(), disease.getAdvice());
        }).sorted(Comparator.comparing(DiseaseScore::getScore).reversed())
          .collect(Collectors.toList());

        if (scores.isEmpty())
            throw new IllegalStateException("Could not compute any disease scores.");

        DiseaseScore top = scores.get(0);

        // Cap confidence at 97
        BigDecimal confidence = top.getScore().min(BigDecimal.valueOf(97));

        // --- Persist prediction ---
        User patient = userRepo.findByUsername(username).orElseThrow();
        Prediction prediction = new Prediction();
        prediction.setPatient(patient);
        prediction.setPredictedDisease(top.getDiseaseName());
        prediction.setConfidenceScore(confidence);
        prediction.setSeverity(top.getSeverity());
        prediction.setAdvice(top.getAdvice());
        prediction.setSymptomsProvided(toJson(inputSymptomNames));
        prediction.setAllResults(toJson(scores));
        predictionRepo.save(prediction);

        return new PredictionResponse(
                prediction.getId(),
                top.getDiseaseName(),
                confidence,
                top.getSeverity(),
                top.getAdvice(),
                inputSymptomNames,
                scores.subList(0, Math.min(5, scores.size())),
                prediction.getPredictedAt()
        );
    }

    // ── Prediction history ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<PredictionHistoryItem> getHistory(String username) {
        User patient = userRepo.findByUsername(username).orElseThrow();
        return predictionRepo.findByPatientIdOrderByPredictedAtDesc(patient.getId())
                .stream().map(p -> {
                    List<String> syms = fromJson(p.getSymptomsProvided());
                    return new PredictionHistoryItem(p.getId(), p.getPredictedDisease(),
                            p.getConfidenceScore(), p.getSeverity(), syms, p.getPredictedAt());
                }).collect(Collectors.toList());
    }

    // ── Doctor: view patient history ───────────────────────────
    @Transactional(readOnly = true)
    public List<PredictionHistoryItem> getPatientHistory(Long patientId) {
        return predictionRepo.findByPatientIdOrderByPredictedAtDesc(patientId)
                .stream().map(p -> new PredictionHistoryItem(p.getId(), p.getPredictedDisease(),
                        p.getConfidenceScore(), p.getSeverity(),
                        fromJson(p.getSymptomsProvided()), p.getPredictedAt()))
                .collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────
    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (JsonProcessingException e) { return "[]"; }
    }

    private List<String> fromJson(String json) {
        try { return objectMapper.readValue(json, new TypeReference<List<String>>() {}); }
        catch (Exception e) { return List.of(); }
    }
}
