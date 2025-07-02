package com.example.demo.branch;

import com.example.demo.disease.DiseaseEntity;
import com.example.demo.doctor.DoctorEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @Size(min = 1, max = 500)
    private String branchLocationLink;
    private String branchImageUrl;
    private String branchDescription;

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "branch_available_days", joinColumns = @JoinColumn(name = "branch_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    @Schema(example = "[\"MONDAY\", \"TUESDAY\", \"WEDNESDAY\"]")
    private List<DayOfWeek> availableDays = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    @JsonIgnore
    private DoctorEntity doctorEntity;

//    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<DiseaseEntity> diseases;

}
