package com.diseasepredictor.repository;

import com.diseasepredictor.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientProfileRepo extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUserId(Long userId);
}
