package com.diseasepredictor.repository;

import com.diseasepredictor.entity.DoctorProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorProfileRepo extends JpaRepository<DoctorProfile, Long> {
    Optional<DoctorProfile> findByUserId(Long userId);
}
