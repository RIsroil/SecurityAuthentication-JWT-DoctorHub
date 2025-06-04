package com.example.demo.doctor;

import com.example.demo.address.AddressEntity;
import com.example.demo.address.AddressRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final JwtService jwtService;
    private final SpecializationRepository specializationRepository;

    @Transactional
    public AuthResponse register(DoctorRegisterRequestDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Bu username allaqachon mavjud");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Bu email allaqachon mavjud");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(request.getUsername());
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        userEntity.setRole(Role.DOCTOR);
        userEntity.setEmail(request.getEmail());
        userRepository.save(userEntity);

        AddressEntity address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        List<SpecializationEntity> specializations = specializationRepository.findAllById(request.getSpecializations());
        if (specializations.isEmpty()) {
            throw new RuntimeException("At least one specialization must be selected");
        }

        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setFirstname(request.getFirstname());
        doctorEntity.setLastname(request.getLastname());
        doctorEntity.setAddress(address);
        doctorEntity.setDateOfBirth(request.getDateOfBirth());
        doctorEntity.setGender(request.getGender());
        doctorEntity.setPhone(request.getPhone());
        doctorEntity.setSpecializationIds(specializations); // ✅ correct
        doctorEntity.setUser(userEntity);
        doctorRepository.save(doctorEntity);

        String accessToken = jwtService.generateAccessToken(userEntity);
        String refreshToken = jwtService.generateRefreshToken(userEntity);
        return new AuthResponse(accessToken, refreshToken);
    }

}