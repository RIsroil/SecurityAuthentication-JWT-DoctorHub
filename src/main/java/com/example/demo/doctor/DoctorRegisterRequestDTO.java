package com.example.demo.doctor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequestDTO {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private Long addressId;
    private String phone;
    @Schema(description = "List of specialization IDs")
    private List<Long> specializations;}
