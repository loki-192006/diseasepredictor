package com.diseasepredictor.repository;

import com.diseasepredictor.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DiseaseRepo extends JpaRepository<Disease, Long> {
    Optional<Disease> findByName(String name);
}
