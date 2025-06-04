package com.example.demo.branch.model;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

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
    private LocalDate atDate;
}
