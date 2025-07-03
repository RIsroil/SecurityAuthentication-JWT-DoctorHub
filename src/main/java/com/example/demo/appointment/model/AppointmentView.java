package com.example.demo.appointment.model;

import com.example.demo.appointment.AppointmentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentView {
    private Long id;
    private Long patientId;
    private String patientName; // Assuming this is patient's first name as per original mapToResponse
    private Long doctorId;
    private String doctorName; // Assuming this is doctor's first name
    private Long branchId;
    private String branchName;
    private Long chatId;
    private Long diseaseId; // Can be null
    private String diseaseName; // Can be null
    private String customReason; // Can be null
    private String price; // e.g., "100 USD", can be null if no disease
    private LocalDate date;
    private LocalTime time;
    private AppointmentStatus status;
    // Removed 'notes' as it wasn't in the original AppointmentResponse
}
