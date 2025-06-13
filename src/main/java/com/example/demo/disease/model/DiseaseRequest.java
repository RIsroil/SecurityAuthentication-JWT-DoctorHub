package com.example.demo.disease.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiseaseRequest {

    @NotBlank
    private String diseaseName;
    @NotBlank
    private Double price;
}
