package com.example.demo.disease;

import com.example.demo.disease.model.DiseaseRequest;
import com.example.demo.disease.model.DiseaseView;

import java.security.Principal;
import java.util.List;

public interface DiseaseService {
    DiseaseView create(Principal principal, Long branchId, DiseaseRequest request);
    List<DiseaseView> getAllDiseasesByDoctor(Principal principal); // Renamed for clarity from original "getAllDiseases"
    DiseaseView getDiseaseById(Long id); // Renamed for clarity
    DiseaseView updateDisease(Principal principal, Long id, DiseaseRequest request);
    void deleteDisease(Principal principal, Long id); // Renamed for clarity
}
