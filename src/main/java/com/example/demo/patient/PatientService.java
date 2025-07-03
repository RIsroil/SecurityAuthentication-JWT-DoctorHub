package com.example.demo.patient;

import com.example.demo.patient.model.PatientView;
import com.example.demo.user.auth.AuthResponse;

public interface PatientService {
    AuthResponse register(PatientRegisterRequestDTO request);
    PatientView getPatientViewById(Long patientId);
}
