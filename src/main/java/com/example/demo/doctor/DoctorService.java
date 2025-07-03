package com.example.demo.doctor;

import com.example.demo.doctor.model.DoctorView;
import com.example.demo.user.auth.AuthResponse;

public interface DoctorService {
    AuthResponse register(DoctorRegisterRequestDTO request);
    DoctorView getDoctorViewById(Long doctorId);
    // Add other methods like updateDoctorProfile, searchDoctors etc. later as needed
}
