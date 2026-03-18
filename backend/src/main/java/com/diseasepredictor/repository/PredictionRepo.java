package com.diseasepredictor.repository;

import com.diseasepredictor.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PredictionRepo extends JpaRepository<Prediction, Long> {
    List<Prediction> findByPatientIdOrderByPredictedAtDesc(Long patientId);
    long countByPatientId(Long patientId);
}
