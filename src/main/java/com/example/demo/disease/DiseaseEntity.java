package com.example.demo.disease;

import com.example.demo.branch.BranchEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseEntity {
    private static final String GENERATOR_NAME = "diseases_gen";
    private static final String SEQUENCE_NAME = "diseases_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String diseaseName;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private BranchEntity branch;

}