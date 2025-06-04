package com.example.demo.user.profile;

import com.example.demo.address.AddressEntity;
import com.example.demo.address.AddressRepository;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.PatientRepository;
import com.example.demo.specialization.SpecializationEntity;
import com.example.demo.specialization.SpecializationRepository;
import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthHelperService;
import com.example.demo.user.profile.dto.ChangePasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AddressRepository addressRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHelperService authHelperService;

    public UserProfileResponse getProfile(String accessToken) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);

        Role role = user.getRole();

        if (role.equals(Role.DOCTOR)) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) throw new RuntimeException("Doctor topilmadi");

            List<String> specializationNames = doctor.getSpecializationIds().stream()
                    .map(SpecializationEntity::getSpecializationName)
                    .toList();

            return UserProfileResponse.builder()
                    .firstname(doctor.getFirstname())
                    .lastname(doctor.getLastname())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .dateOfBirth(doctor.getDateOfBirth())
                    .gender(doctor.getGender())
                    .addressName(doctor.getAddress().getAddressName())
                    .addressLink(doctor.getAddress().getAddressLocationLink())
                    .specializationNames(specializationNames)
                    .languages(doctor.getLanguagesSpoken())
                    .orderFees(doctor.getOrderFees())
                    .educationalBackground(doctor.getEducationalBackground())
                    .phone(doctor.getPhone())
                    .isVerified(doctor.isVerified())
                    .role(Role.DOCTOR.name())
                    .build();
        }

        if (role.equals(Role.PATIENT)) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) throw new RuntimeException("Patient topilmadi");

            return UserProfileResponse.builder()
                    .firstname(patient.getFirstname())
                    .lastname(patient.getLastname())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .dateOfBirth(patient.getDateOfBirth())
                    .gender(patient.getGender())
                    .addressName(patient.getAddress().getAddressName())
                    .addressLink(patient.getAddress().getAddressLocationLink())
                    .phone(patient.getPhone())
                    .role(Role.PATIENT.name())
                    .build();
        }
        throw new RuntimeException("Role noto‘g‘ri: " + role);
    }

    public ResponseEntity<?> updateProfile(String accessToken, ProfileUpdateRequest request) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);

        Role role = user.getRole();

        if (role.equals(Role.DOCTOR)) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) return ResponseEntity.badRequest().body("Doctor topilmadi");

            if (request.getFirstname() != null) doctor.setFirstname(request.getFirstname());
            if (request.getLastname() != null) doctor.setLastname(request.getLastname());
            if (request.getDateOfBirth() != null) doctor.setDateOfBirth(request.getDateOfBirth());
            if (request.getGender() != null) doctor.setGender(request.getGender());
            if (request.getPhone() != null) doctor.setPhone(request.getPhone());
            if (request.getExperienceYears() != null) doctor.setExperienceYears(request.getExperienceYears());

            if (request.getAddressId() != null) {
                AddressEntity address = addressRepository.findById(request.getAddressId())
                        .orElseThrow(() -> new RuntimeException("Address topilmadi"));
                doctor.setAddress(address);
            }
            if (request.getLanguagesSpoken() != null) {doctor.setLanguagesSpoken(request.getLanguagesSpoken());}
            if( request.getOrderFees() != null) doctor.setOrderFees(request.getOrderFees());
            if(request.getEducationalBackground() != null) doctor.setEducationalBackground(request.getEducationalBackground());

            if (request.getSpecializationIds() != null) {
                List<SpecializationEntity> updatedSpecializations = specializationRepository.findAllById(request.getSpecializationIds());
                if (updatedSpecializations.isEmpty()) {
                    return ResponseEntity.badRequest().body("Kamida bitta specialization kerak");
                }
                doctor.setSpecializationIds(updatedSpecializations);
            }

            doctorRepository.save(doctor);
            return ResponseEntity.ok("Doctor profili yangilandi");

        } else if (role.equals(Role.PATIENT)) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) return ResponseEntity.badRequest().body("Patient topilmadi");

            if (request.getFirstname() != null) patient.setFirstname(request.getFirstname());
            if (request.getLastname() != null) patient.setLastname(request.getLastname());
            if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
            if (request.getGender() != null) patient.setGender(request.getGender());
            if (request.getPhone() != null) patient.setPhone(request.getPhone());
            if (request.getAddressId() != null) {
                AddressEntity address = addressRepository.findById(request.getAddressId())
                        .orElseThrow(() -> new RuntimeException("Address topilmadi"));
                patient.setAddress(address);
            }

            patientRepository.save(patient);
            return ResponseEntity.ok("Patient profili yangilandi");

        } else {
            return ResponseEntity.badRequest().body("Role noto‘g‘ri: " + role);
        }
    }

    public ResponseEntity<?> changePassword( String accessToken, ChangePasswordRequest request) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body("Eski parol noto‘g‘ri");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Parol muvaffaqiyatli yangilandi");
    }
}
