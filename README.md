# 🩺 MediPredict — Disease Prediction Web Application

A full-stack healthcare web application that predicts diseases from patient symptoms using a **weighted rule-based engine**. Built with **Java Spring Boot**, **MySQL**, and **vanilla HTML/CSS/JS**.

> ⚠️ **Disclaimer:** This is an educational project. It is NOT a substitute for professional medical advice, diagnosis, or treatment.

---

## 📸 Features

| Feature | Details |
|---|---|
| 🔐 Secure Auth | Spring Security + BCrypt + JWT tokens |
| 🧠 Prediction Engine | Weighted symptom-disease rule mapping |
| 📊 Confidence Score | Normalised score 0–97% per disease |
| 📋 Prediction History | Stored in MySQL, viewable anytime |
| 👤 Patient Dashboard | Profile, history, symptom checker |
| 👨‍⚕️ Doctor Dashboard | View all patients & their histories |
| 💊 Medical Advice | Severity level + advice per disease |
| 📱 Responsive UI | Works on desktop, tablet, mobile |

---

## 🗂️ Project Structure

```
disease-predictor/
├── frontend/                  # Static HTML/CSS/JS
│   ├── index.html             # Landing page
│   ├── login.html             # Login
│   ├── register.html          # Registration
│   ├── patient-dashboard.html # Patient home
│   ├── symptoms.html          # Symptom selector
│   ├── results.html           # Prediction results
│   ├── history.html           # Patient history
│   ├── profile.html           # Patient profile
│   ├── doctor-dashboard.html  # Doctor home
│   ├── doctor-patients.html   # All patients view
│   ├── doctor-profile.html    # Doctor profile
│   ├── css/
│   │   └── styles.css         # Full stylesheet
│   └── js/
│       └── api.js             # API client + utils
│
├── backend/                   # Spring Boot app
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/diseasepredictor/
│       │   ├── DiseasePredictorApplication.java
│       │   ├── entity/        # JPA entities
│       │   ├── repository/    # Spring Data repos
│       │   ├── dto/           # Request/Response DTOs
│       │   ├── service/       # Business logic
│       │   ├── controller/    # REST controllers
│       │   ├── security/      # JWT + UserDetails
│       │   └── config/        # SecurityConfig, ExceptionHandler
│       └── resources/
│           └── application.properties
│
└── database/
    └── schema.sql             # Full MySQL schema + seed data
```

---

## 🚀 Setup Instructions

### Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- A modern browser (Chrome, Firefox, Edge)
- VS Code with Live Server extension (for frontend) OR any HTTP server

---

### Step 1: Database Setup

```bash
# Log in to MySQL
mysql -u root -p

# Run the schema (creates DB, tables, seed data, demo users)
source /path/to/disease-predictor/database/schema.sql;
```

This creates:
- `disease_predictor` database
- All tables (users, patient_profiles, doctor_profiles, symptoms, diseases, mappings, predictions)
- 13 diseases with symptom mappings
- 30 symptoms across 9 categories
- 2 demo users (see below)

---

### Step 2: Configure Backend

Edit `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/disease_predictor?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

app.jwt.secret=DiseasePredictorSecretKey2024VeryLongSecureKeyForHS256Algorithm
app.jwt.expiration-ms=86400000

app.cors.allowed-origins=http://localhost:5500,http://127.0.0.1:5500
```

---

### Step 3: Run the Backend

```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run
```

The API will start at: **http://localhost:8080**

To verify: `curl http://localhost:8080/api/symptoms`

---

### Step 4: Run the Frontend

**Option A — VS Code Live Server (recommended)**
1. Open the `frontend/` folder in VS Code
2. Right-click `index.html` → Open with Live Server
3. Default: `http://127.0.0.1:5500`

**Option B — Python HTTP Server**
```bash
cd frontend
python3 -m http.server 5500
# Open http://localhost:5500
```

**Option C — Node.js serve**
```bash
npm install -g serve
serve frontend/ -p 5500
```

---

### Step 5: Demo Login

| Role | Username | Password |
|------|----------|----------|
| 🧑 Patient | `patient1` | `password123` |
| 👨‍⚕️ Doctor | `dr_sharma` | `password123` |

Use the **Demo Account** buttons on the login page for one-click access.

---

## 🔌 REST API Reference

### Auth (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |

### Symptoms (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/symptoms` | List all 30 symptoms |

### Patient (Requires `ROLE_PATIENT` JWT)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/patient/profile` | Get patient profile |
| PUT | `/api/patient/profile` | Update patient profile |
| POST | `/api/patient/predict` | Run disease prediction |
| GET | `/api/patient/history` | Get prediction history |

### Doctor (Requires `ROLE_DOCTOR` JWT)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/doctor/profile` | Get doctor profile |
| PUT | `/api/doctor/profile` | Update doctor profile |
| GET | `/api/doctor/patients` | List all patients |
| GET | `/api/doctor/patients/{id}/history` | Patient's history |

---

### Sample API Calls

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"patient1","password":"password123"}'
```

**Predict (with token):**
```bash
curl -X POST http://localhost:8080/api/patient/predict \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{"symptoms":["Fever","Cough","Loss of Taste/Smell","Fatigue"]}'
```

---

## 🧠 Prediction Engine Logic

The rule-based engine works as follows:

1. **Input:** List of symptom names from patient
2. **Mapping Lookup:** Fetch all `symptom_disease_mapping` rows matching input symptoms
3. **Score Calculation per Disease:**
   ```
   matched_weight = SUM(weight) of matched symptoms for that disease
   total_weight   = SUM(weight) of ALL symptoms mapped to that disease
   raw_score      = (matched_weight / total_weight) × 100
   ```
4. **Penalty Factor:** Reduces score if patient selected very few symptoms relative to disease complexity
5. **Confidence:** Capped at 97% to prevent false certainty
6. **Output:** Top 5 diseases ranked by score with severity and advice

---

## 🗺️ Diseases Covered

| Disease | Severity |
|---------|----------|
| Common Cold | LOW |
| Influenza (Flu) | MEDIUM |
| COVID-19 | HIGH |
| Pneumonia | HIGH |
| Dengue Fever | HIGH |
| Malaria | HIGH |
| Typhoid | HIGH |
| Allergic Rhinitis | LOW |
| Gastroenteritis | MEDIUM |
| Hypertensive Crisis | CRITICAL |
| Migraine | MEDIUM |
| Tuberculosis | CRITICAL |
| Diabetes (Type 2) | MEDIUM |

---

## 🛡️ Security

- Passwords hashed with **BCrypt** (strength 10)
- **JWT tokens** with configurable expiry (default 24h)
- **Role-based access control** via Spring Security (`PATIENT`, `DOCTOR`)
- **CORS** configured for frontend origin
- **Stateless** session (no server-side sessions)
- **@PreAuthorize** annotations on controller methods

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Frontend | HTML5, CSS3, Vanilla JS |
| Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security, BCrypt, JWT (jjwt) |
| ORM | Spring Data JPA, Hibernate |
| Database | MySQL 8.0 |
| Build | Maven |

---

## 🎓 College Project Notes

This project demonstrates:
- **MVC Architecture** with clear separation of concerns
- **RESTful API design** with proper HTTP methods and status codes
- **Database normalization** (3NF schema design)
- **Security best practices** (hashing, token auth, role-based access)
- **Rule-based AI/Expert System** pattern
- **Responsive web design** without any framework
- **DTO pattern** for clean API contracts

---

## 📝 License

MIT License — Free for educational and personal use.

---

*Built with ❤️ using Spring Boot + MySQL + Vanilla JS*
=======
# diseasepredictor
>>>>>>> 547149f9b437ef619d8746afd381a83287347955
