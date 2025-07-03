package com.example.demo.branch.model;

import com.example.demo.branch.DayOfWeek;
import com.example.demo.disease.model.DiseaseView; // Changed from DiseaseResponse
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class BranchView {
    private Long branchId;
    private String branchName;
    private String branchCity;
    private String branchRegion;
    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;
    private Long doctorId;
    private Set<DayOfWeek> availableDays;
    private List<DiseaseView> diseases; // This will be handled when Disease entity is refactored
}
