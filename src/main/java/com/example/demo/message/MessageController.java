package com.example.demo.message;

import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/{id}")
    public MessageResponse sendMessage(Principal principal, @PathVariable Long id, @RequestBody MessageRequest request) {
        return messageService.sendMessage(principal, id, request, false);
    }

    @PutMapping("/{id}")
    public void updateMessage(Principal principal, @PathVariable Long id, @RequestBody MessageRequest request) {
        messageService.updateMessage(principal, id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(Principal principal, @PathVariable Long id) {
        messageService.deleteMessage(principal, id);
    }
}
