package com.example.demo.appointment;

import com.example.demo.branch.BranchEntity;
import com.example.demo.chat.ChatEntity;
import com.example.demo.disease.DiseaseEntity;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.patient.PatientEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentEntity {

    private static final String GENERATOR_NAME = "appointments_gen";
    private static final String SEQUENCE_NAME = "appointments_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne
    private PatientEntity patient;

    @ManyToOne
    private DoctorEntity doctor;

    @ManyToOne
    private BranchEntity branch;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    @Schema(type = "string", example = "18:00")
    private LocalTime time;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @ManyToOne
    private DiseaseEntity disease;

    private String customReason;

    @ManyToOne
    private ChatEntity chat;
}
