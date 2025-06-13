package com.example.demo.chat;

import com.example.demo.chat.model.ChatResponse;
import com.example.demo.message.model.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping()
    public void createChat(Principal principal, Long doctorId) {
        chatService.createOrGetChat(principal, doctorId);
    }

    @GetMapping()
    public List<ChatResponse> getChatsByUser(Principal principal){
        return chatService.getChatsByUser(principal);
    }

    @GetMapping("/{id}")
    public List<MessageResponse> getChatById(Principal principal, @PathVariable Long id) {
        return chatService.getMessagesByChatId(principal, id);
    }

    @DeleteMapping("/{id}")
    public void deleteChat(Principal principal, @PathVariable Long id) {
        chatService.deleteChat(principal, id);
    }
}
