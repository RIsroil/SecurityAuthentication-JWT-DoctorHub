package com.example.demo.user.profile;

import com.example.demo.user.profile.dto.ChangePasswordRequest;
// We will return Object from getProfile and let controller decide based on instance type,
// or use a wrapper DTO if preferred for Swagger documentation.
// For now, Object is simpler to implement in the service.

import java.security.Principal;

public interface ProfileService {
    Object getProfile(Principal principal); // Returns DoctorView, PatientView, or UserView
    String updateProfile(Principal principal, ProfileUpdateRequest request); // Returns success message
    String changePassword(Principal principal, ChangePasswordRequest request); // Returns success message
}
