package com.example.demo.specialization;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpecializationRepository extends JpaRepository<SpecializationEntity, Long> {
    Optional<SpecializationEntity> findBySpecializationName(String specializationName);
}
