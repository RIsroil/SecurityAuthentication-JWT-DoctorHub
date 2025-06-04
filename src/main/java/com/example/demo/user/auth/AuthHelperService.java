package com.example.demo.user.auth;

import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthHelperService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public UserEntity getUserFromToken(String accessToken) {
        String username = jwtService.extractUsername(accessToken);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));
    }

    public DoctorEntity getDoctorFromToken(String accessToken) {
        UserEntity user = getUserFromToken(accessToken);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new RuntimeException("Doctor topilmadi");
        }
        return doctor;
    }

    public PatientEntity getPatientFromToken(String accessToken) {
        UserEntity user = getUserFromToken(accessToken);
        PatientEntity patient = patientRepository.findByUser_Id(user.getId());
        if (patient == null) {
            throw new RuntimeException("Patient topilmadi");
        }
        return patient;
    }
}

