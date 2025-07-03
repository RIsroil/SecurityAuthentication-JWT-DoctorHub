package com.example.demo.user.profile;

import com.example.demo.doctor.model.DoctorView;
import com.example.demo.patient.model.PatientView;
import com.example.demo.user.model.UserView;
import com.example.demo.user.profile.dto.ChangePasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "User Profile", description = "APIs for managing user profiles and password")
@SecurityRequirement(name = "bearerAuth")
public interface ProfileControllerApi {

    @Operation(summary = "Get current user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved profile",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(oneOf = {DoctorView.class, PatientView.class, UserView.class}))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Profile details not found (e.g., Doctor/Patient record missing for user)")
    })
    @GetMapping("/profile")
    ResponseEntity<Object> getProfile(Principal principal);

    @Operation(summary = "Update current user's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid update data or role not supported for update"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Profile details or related entities (e.g., Address) not found")
    })
    @PatchMapping("/update")
    ResponseEntity<String> updateProfile(Principal principal, @RequestBody ProfileUpdateRequest request);

    @Operation(summary = "Change current user's password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully",
                         content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid old password or new password format incorrect"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/change-password")
    ResponseEntity<String> changePassword(Principal principal, @RequestBody ChangePasswordRequest request);
}
