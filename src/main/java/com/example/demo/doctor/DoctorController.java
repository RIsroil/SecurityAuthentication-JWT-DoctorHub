package com.example.demo.doctor;

import com.example.demo.doctor.model.DoctorView;
import com.example.demo.user.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Added GetMapping, PathVariable

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController implements DoctorControllerApi { // Implements DoctorControllerApi
    private final DoctorService doctorService; // Will inject the interface

    @Override
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerDoctor(@Valid @RequestBody DoctorRegisterRequestDTO request) {
        AuthResponse authResponse = doctorService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @Override
    @GetMapping("/{doctorId}")
    // Add @PreAuthorize if specific roles are needed to view doctor profiles
    public ResponseEntity<DoctorView> getDoctorProfileById(@PathVariable Long doctorId) {
        DoctorView doctorView = doctorService.getDoctorViewById(doctorId);
        return ResponseEntity.ok(doctorView);
    }
}
