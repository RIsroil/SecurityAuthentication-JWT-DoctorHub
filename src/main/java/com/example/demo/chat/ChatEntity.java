package com.example.demo.chat;

import com.example.demo.doctor.DoctorEntity;
import com.example.demo.message.MessageEntity;
import com.example.demo.patient.PatientEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatEntity {

    private static final String GENERATOR_NAME = "chats_gen";
    private static final String SEQUENCE_NAME = "chats_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private DoctorEntity doctor;

    @ManyToOne
    private PatientEntity patient;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<MessageEntity> messages;
}
