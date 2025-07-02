package com.example.demo.branch.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 500)
    private String branchLocationLink;

}
