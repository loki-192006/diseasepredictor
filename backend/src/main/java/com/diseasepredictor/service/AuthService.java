package com.diseasepredictor.service;

import com.diseasepredictor.dto.*;
import com.diseasepredictor.entity.*;
import com.diseasepredictor.repository.*;
import com.diseasepredictor.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Value("${app.institute.password}")
    private String institutePassword;

    @Autowired private AuthenticationManager authManager;
    @Autowired private UserRepo userRepo;
    @Autowired private PatientProfileRepo patientProfileRepo;
    @Autowired private DoctorProfileRepo doctorProfileRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtils jwtUtils;

    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userRepo.findByUsername(req.getUsername()).orElseThrow();
        if (user.getRole() == User.Role.DOCTOR) {
            if (req.getInstitutePassword() == null || !req.getInstitutePassword().equals(institutePassword)) {
                throw new IllegalArgumentException("Invalid institute password for doctor login");
            }
        }

        String token = jwtUtils.generateToken(auth);
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username already taken");
        if (userRepo.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email already registered");

        User.Role role = req.getRole() != null ? req.getRole() : User.Role.PATIENT;
        if (role == User.Role.DOCTOR) {
            if (req.getInstitutePassword() == null || !req.getInstitutePassword().equals(institutePassword)) {
                throw new IllegalArgumentException("Doctor registration requires a valid institute password");
            }
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setRole(role);
        userRepo.save(user);

        // Create empty profile
        if (user.getRole() == User.Role.PATIENT) {
            PatientProfile profile = new PatientProfile();
            profile.setUser(user);
            patientProfileRepo.save(profile);
        } else if (user.getRole() == User.Role.DOCTOR) {
            DoctorProfile profile = new DoctorProfile();
            profile.setUser(user);
            doctorProfileRepo.save(profile);
        }

        String token = jwtUtils.generateTokenFromUsername(user.getUsername());
        return new AuthResponse(token, user.getId(), user.getUsername(),
                user.getEmail(), user.getFullName(), user.getRole().name());
    }
}
