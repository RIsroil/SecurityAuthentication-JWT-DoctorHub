package com.example.demo.address;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "addresses")
@Getter
@Setter
public class AddressEntity {

    private static final String GENERATOR_NAME = "addresses_gen";
    private static final String SEQUENCE_NAME = "addresses_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String addressName;
    private String addressLocationLink;
    private Double latitude;
    private Double longitude;
}
