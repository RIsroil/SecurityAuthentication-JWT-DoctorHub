package com.example.demo.branch.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchRequest {

    private String branchName;

    private String branchRegion;

    private String branchCity;

    @NotBlank(message = "Link https://www.google.com/maps dan olingan bo'lishligi kerak!!!")
    private String branchLocationLink;

}
