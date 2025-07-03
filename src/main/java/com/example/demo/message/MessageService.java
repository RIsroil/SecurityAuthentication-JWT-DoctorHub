package com.example.demo.message;

import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageView;

import java.security.Principal;

public interface MessageService {
    MessageView sendMessage(Principal principal, Long chatId, MessageRequest content, boolean isSystemGenerated);
    MessageView updateMessage(Principal principal, Long messageId, MessageRequest newContent);
    void deleteMessage(Principal principal, Long messageId);
}
