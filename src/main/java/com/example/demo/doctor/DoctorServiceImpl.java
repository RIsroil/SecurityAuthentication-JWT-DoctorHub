package com.example.demo.doctor;

import com.example.demo.address.AddressEntity;
import com.example.demo.address.AddressRepository;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.InvalidInputException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.jwt.JwtService;
import com.example.demo.specialization.SpecializationEntity;
import com.example.demo.specialization.SpecializationRepository;
import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added for readOnly

import com.example.demo.doctor.model.DoctorView; // Added
import com.example.demo.doctor.mapper.DoctorMapper; // Added

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService { // Implements DoctorService
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper = DoctorMapper.INSTANCE; // Added
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JwtService jwtService;
    private final SpecializationRepository specializationRepository;

    @Transactional
    public AuthResponse register(DoctorRegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' already exists.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' already exists.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.DOCTOR);
        userEntity.setEmail(request.getEmail());
        userRepository.save(userEntity);

        AddressEntity address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getAddressId()));

        List<SpecializationEntity> specializations = specializationRepository.findAllById(request.getSpecializations());
        if (specializations.isEmpty()) {
            throw new InvalidInputException("At least one specialization must be selected for a doctor.");
        }

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setFirstname(request.getFirstname());
        doctorEntity.setLastname(request.getLastname());
        doctorEntity.setAddress(address);
        doctorEntity.setDateOfBirth(request.getDateOfBirth());
        doctorEntity.setGender(request.getGender());
        doctorEntity.setPhone(request.getPhone());
        doctorEntity.setSpecializationIds(specializations);
        doctorEntity.setUser(userEntity);
        doctorRepository.save(doctorEntity);

        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);
        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorView getDoctorViewById(Long doctorId) {
        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        // Ensure related entities needed for the view are loaded, e.g., user, address, specializations, certificates
        // This might require @EntityGraph or specific fetching strategies if LAZY loading issues occur.
        // For now, relying on @Transactional to keep session open for MapStruct.
        return doctorMapper.toView(doctorEntity);
    }
}