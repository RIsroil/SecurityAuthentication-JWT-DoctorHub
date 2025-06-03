package com.example.demo.patient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<PatientEntity, Long> {
    PatientEntity findByUser_Id(Long id);
}
