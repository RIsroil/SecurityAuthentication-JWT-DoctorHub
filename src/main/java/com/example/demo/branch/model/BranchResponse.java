package com.example.demo.branch.model;

import com.example.demo.branch.DayOfWeek;
import com.example.demo.disease.model.DiseaseResponse;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class BranchResponse {
    private Long branchId;
    private String branchName;
    private String branchCity;
    private String branchRegion;
    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;
    private Long doctorId;
    private List<DayOfWeek> availableDays;
    //    private List<DiseaseResponse> diseases;
}
