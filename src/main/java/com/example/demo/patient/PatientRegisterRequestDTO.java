package com.example.demo.patient;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class PatientRegisterRequestDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private Long addressId;
    private String phone;
}
