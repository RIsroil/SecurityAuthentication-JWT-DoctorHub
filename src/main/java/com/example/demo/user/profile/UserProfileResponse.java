package com.example.demo.user.profile;

import com.example.demo.user.Languages;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class UserProfileResponse {
    private String firstname;
    private String lastname;
    private String username;
    private String email;
//    private String password;
    private LocalDate dateOfBirth;
    private String gender;
    private String addressName;
    private String addressLink;
    private List<String> specializationNames;
    private String phone;
    private String role;
    private List<Languages> languages;
    private String educationalBackground;
    private Double orderFees;
    private boolean isVerified;
}
