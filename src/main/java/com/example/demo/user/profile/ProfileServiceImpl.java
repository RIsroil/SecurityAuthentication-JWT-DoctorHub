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
import com.example.demo.doctor.mapper.DoctorMapper;       // Added
import com.example.demo.patient.mapper.PatientMapper;     // Added
import com.example.demo.user.mapper.UserMapper;           // Added
import com.example.demo.doctor.model.DoctorView;     // Added
import com.example.demo.patient.model.PatientView;   // Added
import com.example.demo.user.model.UserView;         // Added
import lombok.RequiredArgsConstructor;
// Removed ResponseEntity from service layer imports
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

import com.example.demo.exception.ResourceNotFoundException; // Assuming this custom exception
import org.springframework.transaction.annotation.Transactional; // For read-only transactions

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    // Removed JwtService as it's not used in these methods
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AddressRepository addressRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthHelperService authHelperService;

    private final DoctorMapper doctorMapper = DoctorMapper.INSTANCE;
    private final PatientMapper patientMapper = PatientMapper.INSTANCE;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    @Transactional(readOnly = true)
    public Object getProfile(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        Role role = user.getRole();

        if (role.equals(Role.DOCTOR)) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) throw new ResourceNotFoundException("Doctor profile not found for user: " + user.getUsername());
            // Ensure DoctorEntity's lazy-loaded fields needed by DoctorView are accessible
            // (e.g. user, address, specializations, certificates)
            // @Transactional helps here.
            return doctorMapper.toView(doctor);
        }

        if (role.equals(Role.PATIENT)) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) throw new ResourceNotFoundException("Patient profile not found for user: " + user.getUsername());
            // Ensure PatientEntity's lazy-loaded fields are accessible
            return patientMapper.toView(patient);
        }

        if (role.equals(Role.ADMIN)) {
            return userMapper.toView(user);
        }
        throw new RuntimeException("Unsupported role for profile view: " + role);
    }

    @Override
    @Transactional
    public String updateProfile(Principal principal, ProfileUpdateRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        Role role = user.getRole();

        if (role.equals(Role.DOCTOR)) {
            DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
            if (doctor == null) throw new ResourceNotFoundException("Doctor profile not found for user: " + user.getUsername());

            // Apply updates from request to doctor entity
            if (request.getFirstname() != null) doctor.setFirstname(request.getFirstname());
            if (request.getLastname() != null) doctor.setLastname(request.getLastname());
            if (request.getDateOfBirth() != null) doctor.setDateOfBirth(request.getDateOfBirth());
            if (request.getGender() != null) doctor.setGender(request.getGender());
            if (request.getPhone() != null) doctor.setPhone(request.getPhone());
            if (request.getExperienceYears() != null) doctor.setExperienceYears(request.getExperienceYears());
            if (request.getLanguagesSpoken() != null) doctor.setLanguagesSpoken(request.getLanguagesSpoken());
            if (request.getOrderFees() != null) doctor.setOrderFees(request.getOrderFees());
            if (request.getEducationalBackground() != null) doctor.setEducationalBackground(request.getEducationalBackground());

            if (request.getAddressId() != null) {
                AddressEntity address = addressRepository.findById(request.getAddressId())
                        .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getAddressId()));
                doctor.setAddress(address);
            }

            if (request.getSpecializationIds() != null) {
                List<SpecializationEntity> updatedSpecializations = specializationRepository.findAllById(request.getSpecializationIds());
                // Potentially add check if updatedSpecializations is empty and if that's allowed
                doctor.setSpecializationIds(updatedSpecializations);
            }
            doctorRepository.save(doctor);
            return "Doctor profile updated successfully.";

        } else if (role.equals(Role.PATIENT)) {
            PatientEntity patient = patientRepository.findByUser_Id(user.getId());
            if (patient == null) throw new ResourceNotFoundException("Patient profile not found for user: " + user.getUsername());

            // Apply updates from request to patient entity
            if (request.getFirstname() != null) patient.setFirstname(request.getFirstname());
            if (request.getLastname() != null) patient.setLastname(request.getLastname());
            if (request.getDateOfBirth() != null) patient.setDateOfBirth(request.getDateOfBirth());
            if (request.getGender() != null) patient.setGender(request.getGender());
            if (request.getPhone() != null) patient.setPhone(request.getPhone());

            if (request.getAddressId() != null) {
                AddressEntity address = addressRepository.findById(request.getAddressId())
                        .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + request.getAddressId()));
                patient.setAddress(address);
            }
            patientRepository.save(patient);
            return "Patient profile updated successfully.";
        } else {
            // Admin profile update not handled here, or throw exception
            throw new RuntimeException("Profile update not supported for role: " + role);
        }
    }

    @Override
    @Transactional
    public String changePassword(Principal principal, ChangePasswordRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            // Consider throwing a specific exception for invalid old password
            throw new RuntimeException("Incorrect old password.");
        }
        // Add validation for new password if needed (e.g., strength)
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password updated successfully.";
    }
}
