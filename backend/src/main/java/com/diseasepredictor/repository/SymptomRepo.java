package com.diseasepredictor.repository;

import com.diseasepredictor.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SymptomRepo extends JpaRepository<Symptom, Long> {
    List<Symptom> findAllByOrderByCategoryAscNameAsc();
    List<Symptom> findByNameIn(List<String> names);
}
