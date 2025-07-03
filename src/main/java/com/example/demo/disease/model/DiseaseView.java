package com.example.demo.disease.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DiseaseView {
    private Long id;
    private Long branchId;
    private String branchName;
    private String diseaseName;
    private String price; // Formatted string e.g., "100.0 $"
    // The original DiseaseResponse did not include CurrencyType enum directly,
    // it was used to format the price string. So, DiseaseView also won't have it.
}
