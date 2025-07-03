package com.example.demo.specialization.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpecializationView {
    private Long id;
    private String specializationName;
}
