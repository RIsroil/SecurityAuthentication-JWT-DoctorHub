package com.example.demo.branch.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Data
public class BranchUpdateRequest {

    private String branchName;
    private String branchRegion;
    private String branchCity;

    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2024-04-18")
    private LocalDate atDate;
}
