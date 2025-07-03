package com.example.demo.user.auth;

import com.example.demo.user.auth.dto.ForgotPasswordRequest;
import com.example.demo.user.auth.dto.ResetPasswordRequest;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    AuthResponse login(AuthRequest request);
    AuthResponse refreshToken(String refreshToken);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequest request);
    ResponseEntity<?> resetPassword(String token, ResetPasswordRequest request);
}
