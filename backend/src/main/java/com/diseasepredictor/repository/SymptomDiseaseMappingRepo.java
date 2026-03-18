package com.diseasepredictor.repository;

import com.diseasepredictor.entity.SymptomDiseaseMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SymptomDiseaseMappingRepo extends JpaRepository<SymptomDiseaseMapping, Long> {

    @Query("SELECT m FROM SymptomDiseaseMapping m JOIN FETCH m.disease d JOIN FETCH m.symptom s " +
           "WHERE s.id IN :symptomIds")
    List<SymptomDiseaseMapping> findBySymptomIdIn(@Param("symptomIds") List<Long> symptomIds);

    @Query("SELECT m FROM SymptomDiseaseMapping m JOIN FETCH m.symptom WHERE m.disease.id = :diseaseId")
    List<SymptomDiseaseMapping> findByDiseaseId(@Param("diseaseId") Long diseaseId);
}
