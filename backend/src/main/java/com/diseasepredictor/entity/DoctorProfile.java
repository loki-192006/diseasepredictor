package com.diseasepredictor.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctor_profiles")
@Data
@NoArgsConstructor
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String specialization;

    @Column(length = 200)
    private String qualification;

    @Column(name = "experience_yrs")
    private Integer experienceYrs = 0;

    @Column(name = "license_number", length = 50)
    private String licenseNumber;

    @Column(length = 150)
    private String hospital;
}
