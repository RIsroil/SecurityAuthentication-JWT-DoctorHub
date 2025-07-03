package com.example.demo.appointment;

import com.example.demo.appointment.model.AppointmentRequest;
import com.example.demo.appointment.model.AppointmentView;

import java.security.Principal;
import java.util.List;

public interface AppointmentService {
    AppointmentView create(Principal principal, Long doctorId, AppointmentRequest request);
    List<AppointmentView> getMyAppointments(Principal principal);
    String handleAppointmentAction(Principal principal, Long appointmentId, boolean approveAction);
}
