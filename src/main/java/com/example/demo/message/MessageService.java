package com.example.demo.message;

import com.example.demo.chat.ChatEntity;
import com.example.demo.chat.ChatRepository;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageResponse;
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.Principal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final AuthHelperService authHelperService;

    public MessageResponse sendMessage(Principal principal, Long chatId, MessageRequest content, boolean isSystemGenerated) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        Long replayId = null;
        if (content.getReplayId() != null) {
            MessageEntity message = messageRepository.findById(content.getReplayId()).orElseThrow(() -> new RuntimeException("Main message not found to Replay it"));
            replayId = message.getId();
        }

        MessageEntity message = MessageEntity.builder()
                .chat(chat)
                .sender(user)
                .content(content.getContent())
                .systemGenerated(isSystemGenerated)
                .timestamp(LocalDateTime.now())
                .replayId(replayId)
                .build();
        messageRepository.save(message);
        return MessageResponse.builder()
                .id( message.getId() )
                .content( message.getContent() )
                .senderName( message.getSender().getUsername() )
                .timestamp( message.getTimestamp() )
                .replayId( message.getReplayId() )
                .build();
    }

    public void updateMessage(Principal principal, Long messageId, MessageRequest newContent) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

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
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

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
