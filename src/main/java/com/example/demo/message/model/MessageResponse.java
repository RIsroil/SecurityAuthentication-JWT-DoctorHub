package com.example.demo.message.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private String senderName;
    private LocalDateTime timestamp;
}
