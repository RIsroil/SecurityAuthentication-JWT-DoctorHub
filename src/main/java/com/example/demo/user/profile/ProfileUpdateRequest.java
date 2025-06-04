package com.example.demo.user.profile;

import com.example.demo.user.Languages;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfileUpdateRequest {
    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private Long addressId;

    private Integer experienceYears;
    private List<Long> specializationIds;
    private Double orderFees;
    private List<Languages> languagesSpoken;
    private String educationalBackground;
}
