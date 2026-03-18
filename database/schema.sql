-- ============================================================
--  Disease Prediction App — MySQL Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS disease_predictor;
USE disease_predictor;

-- ── Users (patients & doctors share this table) ─────────────
CREATE TABLE users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        ENUM('PATIENT','DOCTOR') NOT NULL DEFAULT 'PATIENT',
    full_name   VARCHAR(100) NOT NULL,
    phone       VARCHAR(20),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ── Patient profiles ─────────────────────────────────────────
CREATE TABLE patient_profiles (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    date_of_birth   DATE,
    gender          ENUM('MALE','FEMALE','OTHER'),
    blood_group     VARCHAR(5),
    address         TEXT,
    medical_history TEXT,
    allergies       TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Doctor profiles ──────────────────────────────────────────
CREATE TABLE doctor_profiles (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE,
    specialization  VARCHAR(100),
    qualification   VARCHAR(200),
    experience_yrs  INT DEFAULT 0,
    license_number  VARCHAR(50),
    hospital        VARCHAR(150),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ── Symptoms master ──────────────────────────────────────────
CREATE TABLE symptoms (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    category    VARCHAR(50),
    description TEXT
);

-- ── Diseases master ─────────────────────────────────────────
CREATE TABLE diseases (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    severity    ENUM('LOW','MEDIUM','HIGH','CRITICAL') DEFAULT 'MEDIUM',
    advice      TEXT
);

-- ── Symptom → Disease mapping (rule engine) ──────────────────
CREATE TABLE symptom_disease_mapping (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    disease_id  BIGINT NOT NULL,
    symptom_id  BIGINT NOT NULL,
    weight      DECIMAL(3,2) DEFAULT 1.00,   -- 0.00 – 1.00
    FOREIGN KEY (disease_id) REFERENCES diseases(id) ON DELETE CASCADE,
    FOREIGN KEY (symptom_id) REFERENCES symptoms(id) ON DELETE CASCADE,
    UNIQUE KEY uq_dis_sym (disease_id, symptom_id)
);

-- ── Prediction history ───────────────────────────────────────
CREATE TABLE predictions (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id          BIGINT NOT NULL,
    predicted_disease   VARCHAR(150) NOT NULL,
    confidence_score    DECIMAL(5,2) NOT NULL,
    symptoms_provided   TEXT NOT NULL,          -- JSON array of symptom names
    all_results         TEXT,                   -- JSON array of {disease, score}
    severity            ENUM('LOW','MEDIUM','HIGH','CRITICAL'),
    advice              TEXT,
    predicted_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================================
--  Seed Data
-- ============================================================

-- Symptoms
INSERT INTO symptoms (name, category) VALUES
('Fever','General'),('Cough','Respiratory'),('Shortness of Breath','Respiratory'),
('Fatigue','General'),('Headache','Neurological'),('Sore Throat','ENT'),
('Runny Nose','ENT'),('Body Aches','Musculoskeletal'),('Chest Pain','Cardiovascular'),
('Nausea','Gastrointestinal'),('Vomiting','Gastrointestinal'),('Diarrhea','Gastrointestinal'),
('Loss of Appetite','General'),('Skin Rash','Dermatological'),('Joint Pain','Musculoskeletal'),
('Chills','General'),('Sweating','General'),('Dizziness','Neurological'),
('Abdominal Pain','Gastrointestinal'),('Back Pain','Musculoskeletal'),
('Weight Loss','General'),('Night Sweats','General'),('Loss of Taste/Smell','Neurological'),
('Swollen Lymph Nodes','Immunological'),('Sneezing','ENT'),
('Itchy Eyes','ENT'),('Watery Eyes','ENT'),('Muscle Weakness','Musculoskeletal'),
('Confusion','Neurological'),('Palpitations','Cardiovascular');

-- Diseases
INSERT INTO diseases (name, description, severity, advice) VALUES
('Common Cold','Viral infection of the upper respiratory tract.','LOW','Rest, fluids, OTC medication. See doctor if symptoms worsen beyond 10 days.'),
('Influenza (Flu)','Contagious respiratory illness caused by influenza viruses.','MEDIUM','Rest, antiviral medication if caught early. Seek care if breathing difficulty occurs.'),
('COVID-19','Respiratory illness caused by SARS-CoV-2 coronavirus.','HIGH','Isolate, monitor O2 levels, seek immediate care if breathing worsens.'),
('Pneumonia','Infection causing air sac inflammation in one or both lungs.','HIGH','Requires medical evaluation. Antibiotics/antivirals based on cause. Hospitalization may be needed.'),
('Dengue Fever','Mosquito-borne viral disease common in tropical areas.','HIGH','No specific antiviral; supportive care, fluids, pain relievers (NOT aspirin/ibuprofen).'),
('Malaria','Mosquito-borne disease caused by Plasmodium parasites.','HIGH','Prescription antimalarials required. Seek care immediately.'),
('Typhoid','Bacterial infection spread through contaminated food and water.','HIGH','Antibiotics required. Hydration critical. Hospitalization if severe.'),
('Allergic Rhinitis','Allergic reaction causing cold-like symptoms; hay fever.','LOW','Antihistamines, nasal corticosteroids. Identify and avoid allergens.'),
('Gastroenteritis','Inflammation of stomach and intestines; stomach flu.','MEDIUM','ORS, BRAT diet, rest. Seek care if unable to keep fluids down.'),
('Hypertensive Crisis','Dangerously high blood pressure requiring immediate action.','CRITICAL','EMERGENCY — call 911 immediately. Do not drive yourself to hospital.'),
('Migraine','Intense recurring headache often with nausea and light sensitivity.','MEDIUM','Dark quiet room, triptans, OTC pain relief. Consult neurologist for chronic cases.'),
('Tuberculosis','Serious bacterial infection primarily affecting the lungs.','CRITICAL','Long-course antibiotic regimen required. Highly contagious — isolation needed.'),
('Diabetes (Type 2)','Chronic metabolic disorder with high blood sugar levels.','MEDIUM','Lifestyle modification, blood sugar monitoring, possible medication. Regular follow-up essential.');

-- Symptom-Disease Mappings
-- Common Cold
SET @cold=(SELECT id FROM diseases WHERE name='Common Cold');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@cold,(SELECT id FROM symptoms WHERE name='Runny Nose'),1.0),
(@cold,(SELECT id FROM symptoms WHERE name='Sore Throat'),0.9),
(@cold,(SELECT id FROM symptoms WHERE name='Cough'),0.8),
(@cold,(SELECT id FROM symptoms WHERE name='Sneezing'),1.0),
(@cold,(SELECT id FROM symptoms WHERE name='Headache'),0.6),
(@cold,(SELECT id FROM symptoms WHERE name='Fatigue'),0.5),
(@cold,(SELECT id FROM symptoms WHERE name='Fever'),0.4);

-- Influenza
SET @flu=(SELECT id FROM diseases WHERE name='Influenza (Flu)');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@flu,(SELECT id FROM symptoms WHERE name='Fever'),1.0),
(@flu,(SELECT id FROM symptoms WHERE name='Chills'),0.9),
(@flu,(SELECT id FROM symptoms WHERE name='Body Aches'),1.0),
(@flu,(SELECT id FROM symptoms WHERE name='Fatigue'),0.9),
(@flu,(SELECT id FROM symptoms WHERE name='Headache'),0.8),
(@flu,(SELECT id FROM symptoms WHERE name='Cough'),0.7),
(@flu,(SELECT id FROM symptoms WHERE name='Sore Throat'),0.6),
(@flu,(SELECT id FROM symptoms WHERE name='Runny Nose'),0.5);

-- COVID-19
SET @covid=(SELECT id FROM diseases WHERE name='COVID-19');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@covid,(SELECT id FROM symptoms WHERE name='Fever'),0.9),
(@covid,(SELECT id FROM symptoms WHERE name='Cough'),0.9),
(@covid,(SELECT id FROM symptoms WHERE name='Shortness of Breath'),1.0),
(@covid,(SELECT id FROM symptoms WHERE name='Loss of Taste/Smell'),1.0),
(@covid,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8),
(@covid,(SELECT id FROM symptoms WHERE name='Body Aches'),0.7),
(@covid,(SELECT id FROM symptoms WHERE name='Headache'),0.6),
(@covid,(SELECT id FROM symptoms WHERE name='Sore Throat'),0.5);

-- Pneumonia
SET @pneu=(SELECT id FROM diseases WHERE name='Pneumonia');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@pneu,(SELECT id FROM symptoms WHERE name='Fever'),1.0),
(@pneu,(SELECT id FROM symptoms WHERE name='Cough'),1.0),
(@pneu,(SELECT id FROM symptoms WHERE name='Shortness of Breath'),1.0),
(@pneu,(SELECT id FROM symptoms WHERE name='Chest Pain'),0.9),
(@pneu,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8),
(@pneu,(SELECT id FROM symptoms WHERE name='Chills'),0.7),
(@pneu,(SELECT id FROM symptoms WHERE name='Sweating'),0.6);

-- Dengue
SET @dengue=(SELECT id FROM diseases WHERE name='Dengue Fever');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@dengue,(SELECT id FROM symptoms WHERE name='Fever'),1.0),
(@dengue,(SELECT id FROM symptoms WHERE name='Headache'),0.9),
(@dengue,(SELECT id FROM symptoms WHERE name='Joint Pain'),1.0),
(@dengue,(SELECT id FROM symptoms WHERE name='Skin Rash'),0.9),
(@dengue,(SELECT id FROM symptoms WHERE name='Nausea'),0.7),
(@dengue,(SELECT id FROM symptoms WHERE name='Vomiting'),0.7),
(@dengue,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8),
(@dengue,(SELECT id FROM symptoms WHERE name='Body Aches'),0.8);

-- Malaria
SET @mal=(SELECT id FROM diseases WHERE name='Malaria');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@mal,(SELECT id FROM symptoms WHERE name='Fever'),1.0),
(@mal,(SELECT id FROM symptoms WHERE name='Chills'),1.0),
(@mal,(SELECT id FROM symptoms WHERE name='Sweating'),1.0),
(@mal,(SELECT id FROM symptoms WHERE name='Headache'),0.8),
(@mal,(SELECT id FROM symptoms WHERE name='Nausea'),0.7),
(@mal,(SELECT id FROM symptoms WHERE name='Vomiting'),0.7),
(@mal,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8);

-- Typhoid
SET @typ=(SELECT id FROM diseases WHERE name='Typhoid');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@typ,(SELECT id FROM symptoms WHERE name='Fever'),1.0),
(@typ,(SELECT id FROM symptoms WHERE name='Abdominal Pain'),1.0),
(@typ,(SELECT id FROM symptoms WHERE name='Diarrhea'),0.8),
(@typ,(SELECT id FROM symptoms WHERE name='Headache'),0.7),
(@typ,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8),
(@typ,(SELECT id FROM symptoms WHERE name='Loss of Appetite'),0.9),
(@typ,(SELECT id FROM symptoms WHERE name='Nausea'),0.7);

-- Allergic Rhinitis
SET @ar=(SELECT id FROM diseases WHERE name='Allergic Rhinitis');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@ar,(SELECT id FROM symptoms WHERE name='Sneezing'),1.0),
(@ar,(SELECT id FROM symptoms WHERE name='Runny Nose'),1.0),
(@ar,(SELECT id FROM symptoms WHERE name='Itchy Eyes'),1.0),
(@ar,(SELECT id FROM symptoms WHERE name='Watery Eyes'),1.0),
(@ar,(SELECT id FROM symptoms WHERE name='Cough'),0.5),
(@ar,(SELECT id FROM symptoms WHERE name='Sore Throat'),0.4);

-- Gastroenteritis
SET @gas=(SELECT id FROM diseases WHERE name='Gastroenteritis');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@gas,(SELECT id FROM symptoms WHERE name='Nausea'),1.0),
(@gas,(SELECT id FROM symptoms WHERE name='Vomiting'),1.0),
(@gas,(SELECT id FROM symptoms WHERE name='Diarrhea'),1.0),
(@gas,(SELECT id FROM symptoms WHERE name='Abdominal Pain'),0.9),
(@gas,(SELECT id FROM symptoms WHERE name='Fever'),0.6),
(@gas,(SELECT id FROM symptoms WHERE name='Fatigue'),0.6);

-- Migraine
SET @mig=(SELECT id FROM diseases WHERE name='Migraine');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@mig,(SELECT id FROM symptoms WHERE name='Headache'),1.0),
(@mig,(SELECT id FROM symptoms WHERE name='Nausea'),0.8),
(@mig,(SELECT id FROM symptoms WHERE name='Vomiting'),0.6),
(@mig,(SELECT id FROM symptoms WHERE name='Dizziness'),0.7),
(@mig,(SELECT id FROM symptoms WHERE name='Fatigue'),0.5);

-- Tuberculosis
SET @tb=(SELECT id FROM diseases WHERE name='Tuberculosis');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@tb,(SELECT id FROM symptoms WHERE name='Cough'),1.0),
(@tb,(SELECT id FROM symptoms WHERE name='Night Sweats'),1.0),
(@tb,(SELECT id FROM symptoms WHERE name='Weight Loss'),1.0),
(@tb,(SELECT id FROM symptoms WHERE name='Fatigue'),0.8),
(@tb,(SELECT id FROM symptoms WHERE name='Fever'),0.7),
(@tb,(SELECT id FROM symptoms WHERE name='Chest Pain'),0.7),
(@tb,(SELECT id FROM symptoms WHERE name='Shortness of Breath'),0.6);

-- Diabetes
SET @dia=(SELECT id FROM diseases WHERE name='Diabetes (Type 2)');
INSERT INTO symptom_disease_mapping(disease_id,symptom_id,weight) VALUES
(@dia,(SELECT id FROM symptoms WHERE name='Fatigue'),0.9),
(@dia,(SELECT id FROM symptoms WHERE name='Weight Loss'),0.8),
(@dia,(SELECT id FROM symptoms WHERE name='Dizziness'),0.6),
(@dia,(SELECT id FROM symptoms WHERE name='Sweating'),0.5),
(@dia,(SELECT id FROM symptoms WHERE name='Confusion'),0.7);

-- Demo users (password = "password123" BCrypt encoded)
INSERT INTO users (username,email,password,role,full_name,phone) VALUES
('patient1','patient@demo.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU8F4yGi','PATIENT','Arjun Mehta','9876543210'),
('dr_sharma','doctor@demo.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpwTTyU8F4yGi','DOCTOR','Dr. Priya Sharma','9123456789');

INSERT INTO patient_profiles (user_id,date_of_birth,gender,blood_group) VALUES
((SELECT id FROM users WHERE username='patient1'),'1995-06-15','MALE','B+');

INSERT INTO doctor_profiles (user_id,specialization,qualification,experience_yrs,hospital) VALUES
((SELECT id FROM users WHERE username='dr_sharma'),'General Medicine','MBBS, MD','12','City Medical Center');
