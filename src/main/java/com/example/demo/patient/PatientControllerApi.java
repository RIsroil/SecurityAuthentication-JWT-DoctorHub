package com.example.demo.patient;

import com.example.demo.patient.model.PatientView;
import com.example.demo.user.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Patient", description = "Patient registration and profile management APIs")
public interface PatientControllerApi {

    @Operation(summary = "Register a new patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient registered successfully, returns auth tokens"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data (e.g., duplicate username/email, address not found)")
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponse> registerPatient(@Valid @RequestBody PatientRegisterRequestDTO request);

    @Operation(summary = "Get patient profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved patient profile"),
            @ApiResponse(responseCode = "404", description = "Patient not found")
            // Add @ApiResponse(responseCode = "401", ...) if auth is required (e.g. only admin or patient themselves)
    })
    @GetMapping("/{patientId}")
    ResponseEntity<PatientView> getPatientProfileById(@PathVariable Long patientId);
}
