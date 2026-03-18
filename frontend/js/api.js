/* ============================================================
   Disease Predictor — Core JS (api.js)
   ============================================================ */

const API_BASE = 'http://localhost:8080/api';

// ── Token helpers ─────────────────────────────────────────────
const Auth = {
  getToken:   () => localStorage.getItem('dp_token'),
  getUser:    () => JSON.parse(localStorage.getItem('dp_user') || 'null'),
  setSession: (token, user) => {
    localStorage.setItem('dp_token', token);
    localStorage.setItem('dp_user', JSON.stringify(user));
  },
  clear: () => {
    localStorage.removeItem('dp_token');
    localStorage.removeItem('dp_user');
  },
  isLoggedIn: () => !!localStorage.getItem('dp_token'),
  isPatient:  () => Auth.getUser()?.role === 'PATIENT',
  isDoctor:   () => Auth.getUser()?.role === 'DOCTOR',
};

// ── HTTP Client ───────────────────────────────────────────────
async function apiRequest(endpoint, options = {}) {
  const token = Auth.getToken();
  const headers = { 'Content-Type': 'application/json', ...options.headers };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  const data = await response.json();

  if (!response.ok) {
    const message = data.message || `HTTP ${response.status}`;
    throw new Error(message);
  }

  return data;
}

const api = {
  // Auth
  login:    (body)  => apiRequest('/auth/login',    { method: 'POST', body }),
  register: (body)  => apiRequest('/auth/register', { method: 'POST', body }),

  // Symptoms (public)
  getSymptoms: () => apiRequest('/symptoms'),

  // Patient
  getPatientProfile:    ()     => apiRequest('/patient/profile'),
  updatePatientProfile: (body) => apiRequest('/patient/profile', { method: 'PUT', body }),
  predict:              (body) => apiRequest('/patient/predict',  { method: 'POST', body }),
  getHistory:           ()     => apiRequest('/patient/history'),

  // Doctor
  getDoctorProfile:    ()          => apiRequest('/doctor/profile'),
  updateDoctorProfile: (body)      => apiRequest('/doctor/profile', { method: 'PUT', body }),
  getAllPatients:      ()          => apiRequest('/doctor/patients'),
  getPatientById:      (patientId) => apiRequest(`/doctor/patients/${patientId}`),
  updatePatientById:   (patientId, body) => apiRequest(`/doctor/patients/${patientId}`, { method: 'PUT', body }),
  getPatientHistory:   (patientId) => apiRequest(`/doctor/patients/${patientId}/history`),
};

// ── Toast Notifications ───────────────────────────────────────
function showToast(message, type = 'default', duration = 3000) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container';
    document.body.appendChild(container);
  }
  const toast = document.createElement('div');
  toast.className = `toast ${type}`;
  toast.textContent = message;
  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateY(12px)';
    toast.style.transition = 'all .3s ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

// ── Page Loader ───────────────────────────────────────────────
function showLoader() {
  let loader = document.getElementById('page-loader');
  if (!loader) {
    loader = document.createElement('div');
    loader.id = 'page-loader';
    loader.className = 'page-loader';
    loader.innerHTML = `
      <div class="loader-inner">
        <div class="loader-ring"></div>
        <p style="color:var(--slate);font-size:.9rem;font-weight:500;">Loading…</p>
      </div>`;
    document.body.appendChild(loader);
  }
  loader.style.display = 'flex';
}
function hideLoader() {
  const loader = document.getElementById('page-loader');
  if (loader) loader.style.display = 'none';
}

// ── Route Guard ───────────────────────────────────────────────
function requireAuth(role) {
  if (!Auth.isLoggedIn()) { window.location.href = 'login.html'; return false; }
  if (role === 'PATIENT' && !Auth.isPatient()) { window.location.href = 'doctor-dashboard.html'; return false; }
  if (role === 'DOCTOR'  && !Auth.isDoctor())  { window.location.href = 'patient-dashboard.html'; return false; }
  return true;
}

function redirectIfLoggedIn() {
  if (Auth.isLoggedIn()) {
    window.location.href = Auth.isPatient() ? 'patient-dashboard.html' : 'doctor-dashboard.html';
  }
}

// ── Navbar helpers ────────────────────────────────────────────
function buildNavUser() {
  const user = Auth.getUser();
  if (!user) return;
  const el = document.getElementById('nav-user');
  if (!el) return;
  const initials = user.fullName.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase();
  el.innerHTML = `
    <div class="nav-user">
      <div class="avatar">${initials}</div>
      <span>${user.fullName}</span>
      <button class="btn btn-secondary btn-sm" onclick="logout()">Logout</button>
    </div>`;
}

function logout() {
  Auth.clear();
  window.location.href = 'index.html';
}

// ── Severity badge helper ─────────────────────────────────────
function severityBadge(severity) {
  const map = {
    LOW:      'badge-low',
    MEDIUM:   'badge-medium',
    HIGH:     'badge-high',
    CRITICAL: 'badge-critical',
  };
  return `<span class="badge ${map[severity] || 'badge-medium'}">${severity}</span>`;
}

// ── Date formatter ────────────────────────────────────────────
function formatDate(iso) {
  if (!iso) return '—';
  return new Date(iso).toLocaleDateString('en-IN', {
    day: '2-digit', month: 'short', year: 'numeric',
    hour: '2-digit', minute: '2-digit',
  });
}

// ── Animate number count-up ───────────────────────────────────
function countUp(el, target, duration = 900) {
  const start = 0;
  const step = target / (duration / 16);
  let current = start;
  const timer = setInterval(() => {
    current = Math.min(current + step, target);
    el.textContent = Math.round(current);
    if (current >= target) clearInterval(timer);
  }, 16);
}
