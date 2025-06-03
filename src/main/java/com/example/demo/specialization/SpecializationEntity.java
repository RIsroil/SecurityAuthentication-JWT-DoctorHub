package com.example.demo.specialization;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "specializations")
@Getter
@Setter
public class SpecializationEntity {
    private static final String GENERATOR_NAME = "specializations_gen";
    private static final String SEQUENCE_NAME = "specializations_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;
    @Column(unique = true)
    private String specializationName;
}
