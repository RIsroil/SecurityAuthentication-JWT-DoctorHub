package com.example.demo.user.auth;

import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class AuthHelperService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public UserEntity getUserFromPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User topilmadi"));
    }
}

