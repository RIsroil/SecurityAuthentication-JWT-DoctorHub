package com.example.demo.message;

import com.example.demo.message.model.MessageRequest;
import com.example.demo.message.model.MessageResponse; // Controller returns MessageResponse
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Message", description = "Message management APIs")
@SecurityRequirement(name = "bearerAuth")
public interface MessageControllerApi {

    @Operation(summary = "Send a message to a chat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                         content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                         schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or chat ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Chat not found")
    })
    @PostMapping("/{chatId}") // Changed from "/{id}" to "/{chatId}" for clarity
    ResponseEntity<MessageResponse> sendMessage(Principal principal, @PathVariable Long chatId, @RequestBody MessageRequest request);

    @Operation(summary = "Update an existing message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message updated successfully",
                         content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                         schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = MessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or message ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot edit this message (not owner or system message)"),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @PutMapping("/{messageId}") // Changed from "/{id}" to "/{messageId}"
    ResponseEntity<MessageResponse> updateMessage(Principal principal, @PathVariable Long messageId, @RequestBody MessageRequest request);

    @Operation(summary = "Delete a message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message deleted successfully (No content)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Cannot delete this message (not owner or system message)"),
            @ApiResponse(responseCode = "404", description = "Message not found")
    })
    @DeleteMapping("/{messageId}") // Changed from "/{id}" to "/{messageId}"
    ResponseEntity<Void> deleteMessage(Principal principal, @PathVariable Long messageId);
}
