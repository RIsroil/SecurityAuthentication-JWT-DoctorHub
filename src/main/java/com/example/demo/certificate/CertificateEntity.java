package com.example.demo.certificate;


import com.example.demo.certificate.role.CertificateStatus;
import com.example.demo.doctor.DoctorEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateEntity {

    private static final String GENERATOR_NAME = "certificates_gen";
    private static final String SEQUENCE_NAME = "certificates_seq";


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR_NAME)
    @SequenceGenerator(name = GENERATOR_NAME, sequenceName = SEQUENCE_NAME, allocationSize = 1)
    private Long id;

    private String title;
    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private CertificateStatus status = CertificateStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorEntity doctor;
}


