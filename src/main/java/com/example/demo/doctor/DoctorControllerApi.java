package com.example.demo.doctor;

import com.example.demo.doctor.model.DoctorView;
import com.example.demo.user.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Doctor", description = "Doctor registration and profile management APIs")
public interface DoctorControllerApi {

    @Operation(summary = "Register a new doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Doctor registered successfully, returns auth tokens"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data (e.g., duplicate username/email, address not found, no specializations)")
    })
    @PostMapping("/register")
    ResponseEntity<AuthResponse> registerDoctor(@Valid @RequestBody DoctorRegisterRequestDTO request);

    @Operation(summary = "Get doctor profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved doctor profile"),
            @ApiResponse(responseCode = "404", description = "Doctor not found")
            // Add @ApiResponse(responseCode = "401", ...) if auth is required for this endpoint
    })
    @GetMapping("/{doctorId}")
    ResponseEntity<DoctorView> getDoctorProfileById(@PathVariable Long doctorId);
}
