package com.example.demo.user.auth;

import com.example.demo.jwt.JwtService;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.dto.ForgotPasswordRequest;
import com.example.demo.user.auth.dto.ResetLinkResponse;
import com.example.demo.user.auth.dto.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@RequestParam String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Optional<UserEntity> userOptional = userRepository.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("Email ro'yxatdan o'tmagan");
        }

        UserEntity user = userOptional.get();
        String resetToken = jwtService.generateSimpleToken(user); // Oddiy JWT token

        String resetLink = "http://localhost:4200/reset-password?token=" + resetToken;

        ResetLinkResponse link = new ResetLinkResponse();
        link.setMessage(resetLink);
        // String resetLink = "https://yourdomain.com/reset-password?token=" + resetToken;

        // TODO: Email yuborish logikasi shu yerda bo‘ladi (hozircha consolga chiqaramiz)
        System.out.println("Parolni tiklash linki: " + resetLink);

        return ResponseEntity.ok(link);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody ResetPasswordRequest request) {
        String username;
        try {
            username = jwtService.extractUsername(token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri yoki eskirgan");
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Parollar mos emas");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli o‘zgartirildi");
    }

}
