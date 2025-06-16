package com.example.demo.certificate;

import com.example.demo.certificate.role.CertificateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<CertificateEntity, Long> {
    List<CertificateEntity> findAllCertificatesByDoctorId(Long id);

    List<CertificateEntity> findAllCertificatesByStatus(CertificateStatus status);
}
