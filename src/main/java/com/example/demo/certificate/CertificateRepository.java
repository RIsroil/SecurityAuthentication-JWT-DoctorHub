package com.example.demo.certificate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {
    List<CertificateEntity> findAllCertificatesByDoctorId(Long id);
}
