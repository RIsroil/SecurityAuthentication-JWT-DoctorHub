package com.example.demo.appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findAllByDoctor_Id(Long doctorId);

    List<AppointmentEntity> findAllByPatient_Id(Long patientId);

    boolean existsByPatientIdAndBranchIdAndDateAndDiseaseId(Long patientId, Long branchId, LocalDate date, Long diseaseId);

}
