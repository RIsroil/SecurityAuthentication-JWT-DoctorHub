package com.example.demo.user.auth;

import com.example.demo.user.auth.dto.ForgotPasswordRequest;
import com.example.demo.user.auth.dto.ResetPasswordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Authentication", description = "User authentication and password management APIs")
@RequestMapping("/auth") // Base path for all auth operations
public interface AuthControllerApi {

    @Operation(summary = "Login a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, returns auth tokens"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or user not found")
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request);

    @Operation(summary = "Refresh access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refresh successful, returns new access token and original refresh token"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken);

    @Operation(summary = "Request a password reset link")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent if email exists"),
            @ApiResponse(responseCode = "400", description = "Email not registered")
    })
    @PostMapping("/forgot-password")
    ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request);

    @Operation(summary = "Reset password using a token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token, passwords do not match, or other error")
    })
    @PostMapping("/reset-password")
    ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request);
}
