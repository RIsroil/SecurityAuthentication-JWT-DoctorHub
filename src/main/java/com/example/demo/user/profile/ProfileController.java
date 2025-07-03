package com.example.demo.user.profile;

import com.example.demo.user.profile.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Principal principal) {
        return ResponseEntity.ok(profileService.getProfile(principal));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(Principal principal, @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(principal, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileService.changePassword(principal, request));
    }
}
