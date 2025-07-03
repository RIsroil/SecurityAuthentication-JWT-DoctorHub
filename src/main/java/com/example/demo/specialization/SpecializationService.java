package com.example.demo.specialization;

import com.example.demo.specialization.model.SpecializationView;

import java.util.List;

public interface SpecializationService {
    SpecializationView createSpecialization(RequestSpecialization requestSpecialization);
    void deleteSpecialization(Long id);
    SpecializationView getSpecializationById(Long id);
    List<SpecializationView> getAllSpecializations();
    SpecializationView updateSpecialization(Long id, RequestSpecialization requestSpecialization);
}
