package com.example.demo.patient;

import com.example.demo.patient.model.PatientView;
import com.example.demo.user.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Added GetMapping, PathVariable

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController implements PatientControllerApi { // Implements PatientControllerApi
    private final PatientService patientService; // Will inject the interface

    @Override
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody PatientRegisterRequestDTO request) {
        AuthResponse authResponse = patientService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @GetMapping("/{patientId}")
    // Add @PreAuthorize if specific roles are needed to view patient profiles
    public ResponseEntity<PatientView> getPatientProfileById(@PathVariable Long patientId) {
        PatientView patientView = patientService.getPatientViewById(patientId);
        return ResponseEntity.ok(patientView);
    }
}
