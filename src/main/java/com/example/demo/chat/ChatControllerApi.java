package com.example.demo.chat;

import com.example.demo.chat.model.ChatResponse; // Controller returns ChatResponse
import com.example.demo.message.model.MessageResponse; // Controller returns MessageResponse
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@Tag(name = "Chat", description = "Chat management APIs")
@SecurityRequirement(name = "bearerAuth")
public interface ChatControllerApi {

    @Operation(summary = "Create or get a chat with a doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat created or retrieved successfully (no content returned by original controller)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Doctor or Patient record not found")
    })
    @PostMapping()
    ResponseEntity<Void> createOrGetChat(Principal principal, @RequestParam Long doctorId); // Changed to RequestParam based on typical usage, adjust if it was body

    @Operation(summary = "Get all chats for the current user (patient or doctor)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chats"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping()
    ResponseEntity<List<ChatResponse>> getChatsByUser(Principal principal);

    @Operation(summary = "Get all messages within a specific chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved messages"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not part of this chat"),
            @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    @GetMapping("/{id}")
    ResponseEntity<List<MessageResponse>> getMessagesByChatId(Principal principal, @PathVariable Long id);

    @Operation(summary = "Delete a chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chat deleted successfully (no content returned by original controller)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not part of this chat or cannot delete due to appointment status"),
            @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteChat(Principal principal, @PathVariable Long id) throws AccessDeniedException;
}
