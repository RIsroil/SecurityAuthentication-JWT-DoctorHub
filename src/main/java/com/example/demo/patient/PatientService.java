package com.example.demo.patient;

import com.example.demo.address.AddressEntity;
import com.example.demo.address.AddressRepository;
import com.example.demo.exception.DuplicateResourceException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.jwt.JwtService;
import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientRepository patientRepository;
    private final AddressRepository addressRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(PatientRegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' already exists.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' already exists.");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.PATIENT);
        userEntity.setEmail(request.getEmail());
        userRepository.save(userEntity);

        AddressEntity address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getAddressId()));

        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setFirstname(request.getFirstname());
        patientEntity.setLastname(request.getLastname());
        patientEntity.setAddress(address);
        patientEntity.setDateOfBirth(request.getDateOfBirth());
        patientEntity.setGender(request.getGender());
        patientEntity.setPhone(request.getPhone());
        patientEntity.setUser(userEntity);

        patientRepository.save(patientEntity);
        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);

        return new AuthResponse(accessToken, refreshToken);
    }
}
