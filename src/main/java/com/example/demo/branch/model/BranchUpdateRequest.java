package com.example.demo.branch.model;

import com.example.demo.branch.DayOfWeek;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

//import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Data
public class BranchUpdateRequest {

    private String branchName;
    private String branchRegion;
    private String branchCity;

    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;

    @Schema(example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]")
    private List<DayOfWeek> availableDays;
}
