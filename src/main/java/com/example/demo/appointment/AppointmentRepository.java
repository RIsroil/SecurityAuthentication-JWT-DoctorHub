package com.example.demo.appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findAllByDoctor_Id(Long doctorId);

    List<AppointmentEntity> findAllByPatient_Id(Long patientId);

    List<AppointmentEntity> findAllByChat_Id(Long chatId);

}
