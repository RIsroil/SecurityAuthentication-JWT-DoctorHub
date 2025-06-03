package com.example.demo.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorEntity, Long> {
    DoctorEntity findByUser_Id(Long id);
}
