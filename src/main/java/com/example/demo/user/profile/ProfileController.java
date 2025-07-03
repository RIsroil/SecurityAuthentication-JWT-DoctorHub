package com.example.demo.user.profile;

import com.example.demo.user.profile.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // Assuming PreAuthorize for authenticated access

import java.security.Principal;

@RestController
@RequestMapping("/auth") // Base path from original controller
@RequiredArgsConstructor
public class ProfileController implements ProfileControllerApi {

    private final ProfileService profileService; // Will be the interface

    @Override
    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()") // Ensures user is authenticated
    public ResponseEntity<Object> getProfile(Principal principal) {
        Object profileView = profileService.getProfile(principal);
        return ResponseEntity.ok(profileView);
    }

    @Override
    @PatchMapping("/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateProfile(Principal principal, @RequestBody ProfileUpdateRequest request) {
        String message = profileService.updateProfile(principal, request);
        return ResponseEntity.ok(message);
    }

    @Override
    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> changePassword(Principal principal, @RequestBody ChangePasswordRequest request) {
        String message = profileService.changePassword(principal, request);
        return ResponseEntity.ok(message);
    }
}
