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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ProfileController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AddressRepository addressRepository;
    private final SpecializationRepository specializationRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String accessToken) {
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri: " + e.getMessage());
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        String role = user.getRole();

        if (role.equalsIgnoreCase("DOCTOR")) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) return ResponseEntity.badRequest().body("Doctor topilmadi");

            List<String> specializationNames = doctor.getSpecializationIds().stream()
                    .map(SpecializationEntity::getSpecializationName)
                    .toList();


            return ResponseEntity.ok(UserProfileResponse.builder()
                    .firstname(doctor.getFirstname())
                    .lastname(doctor.getLastname())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .dateOfBirth(doctor.getDateOfBirth())
                    .gender(doctor.getGender())
                    .addressName(doctor.getAddress().getAddressName())
                    .addressLink(doctor.getAddress().getAddressLocationLink())
                    .specializationNames(specializationNames)
                    .phone(doctor.getPhone())
                    .role("DOCTOR")
                    .build());
        }

        if (role.equalsIgnoreCase("PATIENT")) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) return ResponseEntity.badRequest().body("Patient topilmadi");

            return ResponseEntity.ok(UserProfileResponse.builder()
                    .firstname(patient.getFirstname())
                    .lastname(patient.getLastname())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .password(user.getPassword())
                    .dateOfBirth(patient.getDateOfBirth())
                    .gender(patient.getGender())
                    .addressName(patient.getAddress().getAddressName())
                    .addressLink(patient.getAddress().getAddressLocationLink())
                    .phone(patient.getPhone())
                    .role("PATIENT")
                    .build());
        }

        return ResponseEntity.badRequest().body("Role noto‘g‘ri: " + role);
    }


    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(
            @RequestParam String accessToken,
            @RequestBody ProfileUpdateRequest request
    ) {
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri: " + e.getMessage());
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        String role = user.getRole();

        if (role.equalsIgnoreCase("DOCTOR")) {
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

            // 🔥 Mutaxassisliklarni yangilash
            if (request.getSpecializationIds() != null) {
                List<SpecializationEntity> updatedSpecializations = specializationRepository.findAllById(request.getSpecializationIds());
                if (updatedSpecializations.isEmpty()) {
                    return ResponseEntity.badRequest().body("Kamida bitta specialization kerak");
                }
                doctor.setSpecializationIds(updatedSpecializations);

            }

            doctorRepository.save(doctor);
            return ResponseEntity.ok("Doctor profili yangilandi");

        } else if (role.equalsIgnoreCase(Role.PATIENT.name())) {
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
}
