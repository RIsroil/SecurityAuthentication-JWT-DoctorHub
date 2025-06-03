package com.example.demo.doctor;

import com.example.demo.user.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody DoctorRegisterRequestDTO request) {
        return doctorService.register(request);
    }
}
