package com.example.demo.appointment.model;

import com.example.demo.appointment.AppointmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
public class AppointmentResponse {
    private Long id;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private Long branchId;
    private String branchName;
    private Long diseaseId;
    private String diseaseName;
    private LocalDate date;
    private LocalTime time;
    private String customReason;
    private AppointmentStatus status;
}
