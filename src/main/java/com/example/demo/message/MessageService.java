package com.example.demo.message;

import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.ChatRepository;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageResponse;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public MessageResponse sendMessage(Principal principal, Long chatId, MessageRequest content, boolean isSystemGenerated) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        MessageEntity message = MessageEntity.builder()
                .chat(chat)
                .sender(user)
                .content(content.getContent())
                .systemGenerated(isSystemGenerated)
                .timestamp(LocalDateTime.now())
                .build();
        messageRepository.save(message);
        return MessageResponse.builder()
                .id( message.getId() )
                .content( message.getContent() )
                .senderName( message.getSender().getUsername() )
                .timestamp( message.getTimestamp() )
                .build();
    }

    public void updateMessage(Principal principal, Long messageId, MessageRequest newContent) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.isSystemGenerated()) {
            throw new RuntimeException("System message cannot be modified");
        }

        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only edit your own messages");
        }

        message.setContent(newContent.getContent());
        messageRepository.save(message);
    }

    public void deleteMessage(Principal principal, Long messageId) {
        String username = principal.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));

        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (message.isSystemGenerated()) {
            throw new RuntimeException("System message cannot be deleted");
        }
        if (!message.getSender().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own messages");
        }

        messageRepository.delete(message);
    }
}
