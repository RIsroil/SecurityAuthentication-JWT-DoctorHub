package com.example.demo.chat.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatView {
    private Long chatId;
    private Long doctorId;
    private String doctorName;
    private Long patientId;
    private String patientName;
    private LocalDateTime createdAt;
    // Note: List<MessageView> might be added here later if needed for a combined view,
    // but ChatService.getMessagesByChatId currently returns List<MessageResponse> directly.
}
