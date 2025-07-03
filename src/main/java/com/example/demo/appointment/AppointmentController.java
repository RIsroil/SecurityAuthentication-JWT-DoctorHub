package com.example.demo.appointment;

import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping("/{doctorId}")
    public AppointmentResponse createAppointment(Principal principal, @PathVariable Long doctorId, @RequestBody AppointmentRequest request){
        return appointmentService.create(principal, doctorId, request);
    }

    @GetMapping()
    public List<AppointmentResponse> getAllAppointments(Principal principal){
        return appointmentService.getMyAppointments(principal);
    }

    @PatchMapping("/{id}")
    public String updateAppointment(Principal principal, @PathVariable Long id, boolean status){
        return appointmentService.handleAppointmentAction(principal, id, status);
    }

}
