package com.example.demo.message;

import com.example.demo.chat.ChatEntity;
import com.example.demo.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    private static final String GENERATOR_NAME = "messages_gen";
    private static final String SEQUENCE_NAME = "messages_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private ChatEntity chat;

    @ManyToOne
    @JsonIgnore
    private UserEntity sender;

    private String content;

    private LocalDateTime timestamp;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean systemGenerated = false;

}
