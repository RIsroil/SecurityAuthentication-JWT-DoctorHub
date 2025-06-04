package com.example.demo.branch;

import com.example.demo.doctor.DoctorEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {

    private static final String GENERATOR_NAME = "branches_gen";
    private static final String SEQUENCE_NAME = "branches_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String branchName;
    private String branchRegion;
    private String branchCity;

    private Double latitude;
    private Double longitude;

    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(type = "string", example = "2024-04-18")
    private LocalDate atDate;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private DoctorEntity doctorEntity;

}
