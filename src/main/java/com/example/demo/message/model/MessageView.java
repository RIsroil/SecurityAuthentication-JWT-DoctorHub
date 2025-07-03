package com.example.demo.message.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageView {
    private Long id;
    private Long chatId; // For context, though not in original MessageResponse directly
    private String senderName; // Username of the sender
    private Long senderId; // ID of the sender UserEntity
    private String content;
    private LocalDateTime timestamp;
    private boolean systemGenerated;
}
