package com.example.demo.disease.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DiseaseResponse {
    private Long id;
    private Long branchId;
    private String branchName;
    private String diseaseName;
    private Double price;
}
