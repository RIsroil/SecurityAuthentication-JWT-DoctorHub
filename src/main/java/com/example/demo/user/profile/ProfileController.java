package com.example.demo.user.profile;

import com.example.demo.user.profile.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String accessToken) {
        return ResponseEntity.ok(profileService.getProfile(accessToken));
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestParam String accessToken, @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateProfile(accessToken, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String accessToken, @RequestBody ChangePasswordRequest request) {
        return ResponseEntity.ok(profileService.changePassword(accessToken, request));
    }
}
