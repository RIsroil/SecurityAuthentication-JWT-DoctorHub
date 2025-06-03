package com.example.demo.doctor;

import com.example.demo.address.AddressEntity;
import com.example.demo.specialization.SpecializationEntity;
import com.example.demo.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "doctor")
@Getter
@Setter
public class DoctorEntity {
    private static final String GENERATOR_NAME = "doctors_gen";
    private static final String SEQUENCE_NAME = "doctors_seq";

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doctor_specializations",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "specialization_id")
    )
    private List<SpecializationEntity> specializationIds;

    private int experienceYears;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
