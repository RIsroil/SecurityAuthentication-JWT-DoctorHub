package com.example.demo.appointment;

import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "Appointment", description = "Appointment management APIs")
@SecurityRequirement(name = "bearerAuth") // Assuming auth is still required
public interface AppointmentControllerApi {

    @Operation(summary = "Create a new appointment with a specific doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created appointment"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Patient, Doctor or Branch not found") // Branch might be part of request or derived
    })
    ResponseEntity<AppointmentView> createAppointment(Principal principal, @PathVariable Long doctorId, @RequestBody AppointmentRequest request);

    @Operation(summary = "Get all appointments for the current user (patient or doctor)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved appointments"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    ResponseEntity<List<AppointmentView>> getAllAppointments(Principal principal);

    @Operation(summary = "Update appointment status (e.g., confirm/cancel)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated appointment status"),
            @ApiResponse(responseCode = "400", description = "Invalid action or status"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User cannot perform this action"),
            @ApiResponse(responseCode = "404", description = "Appointment not found")
    })
    ResponseEntity<String> updateAppointment(Principal principal, @PathVariable Long id, @RequestParam boolean action); // Assuming 'status' maps to an action like confirm (true) or cancel (false)
}
