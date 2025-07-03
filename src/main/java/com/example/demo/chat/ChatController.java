package com.example.demo.chat;

import com.example.demo.chat.model.ChatResponse;
import com.example.demo.chat.mapper.ChatMapper; // For mapping View to Response
import com.example.demo.message.model.MessageResponse;
// import com.example.demo.message.mapper.MessageMapper; // For mapping MessageView to MessageResponse when available
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController implements ChatControllerApi {

    private final ChatService chatService;
    private final ChatMapper chatMapper = ChatMapper.INSTANCE; // For converting ChatView to ChatResponse
    // private final MessageMapper messageMapper = MessageMapper.INSTANCE; // When Message is refactored

    @Override
    @PostMapping()
    @PreAuthorize("hasRole('PATIENT')") // Typically a patient initiates a chat with a doctor
    public ResponseEntity<Void> createOrGetChat(Principal principal, @RequestParam Long doctorId) {
        chatService.createOrGetChat(principal, doctorId);
        return ResponseEntity.ok().build(); // Or ResponseEntity.status(HttpStatus.CREATED/OK) if a ChatResponse was returned
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<ChatResponse>> getChatsByUser(Principal principal) {
        // Service returns List<ChatView>, controller needs to map to List<ChatResponse>
        List<ChatResponse> chatResponses = chatMapper.toChatResponseList(chatService.getChatsByUser(principal));
        return ResponseEntity.ok(chatResponses);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // Authorization handled in service
    public ResponseEntity<List<MessageResponse>> getMessagesByChatId(Principal principal, @PathVariable Long id) {
        // Service currently returns List<MessageResponse> directly.
        // When MessageService is refactored to return List<MessageView>, this will need mapping.
        List<MessageResponse> messages = chatService.getMessagesByChatId(principal, id);
        return ResponseEntity.ok(messages);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')") // Authorization handled in service
    public ResponseEntity<Void> deleteChat(Principal principal, @PathVariable Long id) throws AccessDeniedException {
        chatService.deleteChat(principal, id);
        return ResponseEntity.ok().build();
    }
}
