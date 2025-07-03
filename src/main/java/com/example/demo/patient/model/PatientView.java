package com.example.demo.patient.model;

import com.example.demo.address.model.AddressView;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientView {
    private Long id;
    private String firstname;
    private String lastname;
    private String username; // from UserEntity
    private String email; // from UserEntity
    private LocalDate dateOfBirth;
    private String gender;
    private AddressView address; // Using AddressView
    private String phone;
}
