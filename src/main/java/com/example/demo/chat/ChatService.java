package com.example.demo.chat;

import com.example.demo.chat.model.ChatView;
import com.example.demo.message.model.MessageResponse; // Using existing MessageResponse for now

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

public interface ChatService {
    ChatView createOrGetChat(Principal principal, Long doctorId);
    List<ChatView> getChatsByUser(Principal principal);
    void deleteChat(Principal principal, Long chatId) throws AccessDeniedException;
    List<MessageResponse> getMessagesByChatId(Principal principal, Long chatId); // Returning existing MessageResponse for now
}
