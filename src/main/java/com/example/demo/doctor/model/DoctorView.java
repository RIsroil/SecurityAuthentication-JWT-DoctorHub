package com.example.demo.doctor.model;

import com.example.demo.address.model.AddressView;
import com.example.demo.certificate.model.CertificateView; // Assuming CertificateView is available
import com.example.demo.user.Languages;
import com.example.demo.specialization.model.SpecializationView; // To be added later

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DoctorView {
    private Long id;
    private String firstname;
    private String lastname;
    private String username; // from UserEntity
    private String email; // from UserEntity
    private LocalDate dateOfBirth;
    private String gender;
    private AddressView address; // Using AddressView
    private String phone;
    private int experienceYears;
    private Double orderFees;
    private List<Languages> languagesSpoken;
    private String educationalBackground;
    private boolean isVerified;
    private List<SpecializationView> specializations; // Placeholder for List<SpecializationView>
    private List<CertificateView> certificates; // Using CertificateView
}
