package com.example.demo.patient;

import com.example.demo.address.AddressEntity;
import com.example.demo.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient")
@Getter
@Setter
public class PatientEntity {

    private static final String GENERATOR_NAME = "patients_gen";
    private static final String SEQUENCE_NAME = "patients_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String firstname;
    private String lastname;
    private LocalDate dateOfBirth;
    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressEntity address;

    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
