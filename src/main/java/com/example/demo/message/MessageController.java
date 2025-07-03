package com.example.demo.message;

import com.example.demo.message.mapper.MessageMapper;
import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageResponse;
import com.example.demo.message.model.MessageView;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Assuming PreAuthorize is used
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageController implements MessageControllerApi {

    private final MessageService messageService;
    private final MessageMapper messageMapper = MessageMapper.INSTANCE;

    @Override
    @PostMapping("/{chatId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // Or more specific based on who can send messages
    public ResponseEntity<MessageResponse> sendMessage(Principal principal, @PathVariable Long chatId, @RequestBody MessageRequest request) {
        MessageView messageView = messageService.sendMessage(principal, chatId, request, false);
        return ResponseEntity.ok(messageMapper.toMessageResponse(messageView));
    }

    @Override
    @PutMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // More specific auth in service (sender only)
    public ResponseEntity<MessageResponse> updateMessage(Principal principal, @PathVariable Long messageId, @RequestBody MessageRequest request) {
        MessageView messageView = messageService.updateMessage(principal, messageId, request);
        return ResponseEntity.ok(messageMapper.toMessageResponse(messageView));
    }

    @Override
    @DeleteMapping("/{messageId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // More specific auth in service (sender only)
    public ResponseEntity<Void> deleteMessage(Principal principal, @PathVariable Long messageId) {
        messageService.deleteMessage(principal, messageId);
        return ResponseEntity.ok().build();
    }
}
