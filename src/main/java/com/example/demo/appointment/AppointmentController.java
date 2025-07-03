package com.example.demo.appointment;

import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/appointment") // Matching the original controller's request mapping
@RequiredArgsConstructor
public class AppointmentController implements AppointmentControllerApi {

    private final AppointmentService appointmentService;

    @PostMapping("/{doctorId}")
    @PreAuthorize("hasRole('PATIENT')") // Ensuring only patients can create
    @Override
    public ResponseEntity<AppointmentView> createAppointment(Principal principal, @PathVariable Long doctorId, @RequestBody AppointmentRequest request) {
        AppointmentView appointmentView = appointmentService.create(principal, doctorId, request);
        return new ResponseEntity<>(appointmentView, HttpStatus.CREATED);
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // Both can view their appointments
    @Override
    public ResponseEntity<List<AppointmentView>> getAllAppointments(Principal principal) {
        List<AppointmentView> appointments = appointmentService.getMyAppointments(principal);
        return ResponseEntity.ok(appointments);
    }

    @PatchMapping("/{id}")
    // @PreAuthorize will be handled by logic within the service based on 'action' and user role
    // More specific PreAuthorize could be added if action parameter was more descriptive (e.g., /approve, /cancel)
    @Override
    public ResponseEntity<String> updateAppointment(Principal principal, @PathVariable Long id, @RequestParam boolean action) {
        String message = appointmentService.handleAppointmentAction(principal, id, action);
        return ResponseEntity.ok(message);
    }
}
