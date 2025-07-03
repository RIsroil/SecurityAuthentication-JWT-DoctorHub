package com.example.demo.message;

import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.ChatRepository;
import com.example.demo.exception.ResourceNotFoundException; // Assuming
import com.example.demo.message.mapper.MessageMapper;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageView;
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException; // Standard Spring Security exception
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final AuthHelperService authHelperService;
    private final MessageMapper messageMapper = MessageMapper.INSTANCE;

    @Override
    @Transactional
    public MessageView sendMessage(Principal principal, Long chatId, MessageRequest content, boolean isSystemGenerated) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found with ID: " + chatId));

        // Authorization: Ensure the user is part of the chat?
        // Original service didn't explicitly check if sender is part of chat, but it's good practice.
        // For now, will rely on principal being correctly associated or further checks if needed.
        // If systemGenerated is true, user might be an admin or system user.

        MessageEntity message = MessageEntity.builder()
                .chat(chat)
                .sender(user) // If systemGenerated, sender might be a specific system user or null
                .content(content.getContent())
                .systemGenerated(isSystemGenerated)
                .timestamp(LocalDateTime.now())
                .build();
        MessageEntity savedMessage = messageRepository.save(message);
        return messageMapper.toView(savedMessage);
    }

    @Override
    @Transactional
    public MessageView updateMessage(Principal principal, Long messageId, MessageRequest newContent) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (message.isSystemGenerated()) {
            throw new AccessDeniedException("System-generated messages cannot be modified.");
        }

        if (!message.getSender().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only edit your own messages.");
        }

        message.setContent(newContent.getContent());
        // message.setTimestamp(LocalDateTime.now()); // Optionally update timestamp on edit
        MessageEntity updatedMessage = messageRepository.save(message);
        return messageMapper.toView(updatedMessage);
    }

    @Override
    @Transactional
    public void deleteMessage(Principal principal, Long messageId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        MessageEntity message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with ID: " + messageId));

        if (message.isSystemGenerated()) {
            // Or allow admins to delete system messages
            throw new AccessDeniedException("System-generated messages cannot be deleted by users.");
        }

        // Add role check: e.g. Admin can delete any message, or only sender can delete.
        // Current logic: only sender can delete.
        if (!message.getSender().getId().equals(user.getId())) {
            throw new AccessDeniedException("You can only delete your own messages.");
        }

        messageRepository.delete(message);
    }
}
