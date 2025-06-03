package com.example.demo.user.profile;

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
    private Integer experienceYears; // faqat doctor uchun
    private List<Long> specializationIds; // 🔥 Doctor uchun yangi qo‘shildi


}
