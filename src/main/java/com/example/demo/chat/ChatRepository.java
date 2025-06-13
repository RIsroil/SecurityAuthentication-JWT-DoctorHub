package com.example.demo.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {

    Optional<ChatEntity> findByDoctor_IdAndPatient_Id(Long doctorId, Long patientId);
    List<ChatEntity> findAllByDoctor_Id(Long doctorId);
    List<ChatEntity> findAllByPatient_Id(Long patientId);


}
