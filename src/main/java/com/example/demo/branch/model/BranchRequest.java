package com.example.demo.branch.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequest {

    @NotBlank
    private String branchName;
    @NotBlank
    private String branchRegion;
    @NotBlank
    private String branchCity;
    @NotBlank
    private String branchLocationLink;

}
