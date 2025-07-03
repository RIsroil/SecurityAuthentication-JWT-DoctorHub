package com.example.demo.certificate;

import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateView;
import com.example.demo.certificate.role.CertificateStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

public interface CertificateService {
    String uploadCertificateFile(MultipartFile file); // Returns file URL or an identifier
    CertificateView addCertificate(Principal principal, CertificateRequest request);
    List<CertificateView> getMyCertificates(Principal principal);
    String updateCertificateStatus(Long certificateId, CertificateStatus status); // Returns a success message
    List<CertificateView> getDoctorAllCertificatesByDoctorId(Long doctorId);
    String deleteCertificate(Principal principal, Long certificateId); // Returns a success/error message
    List<CertificateView> getCertificatesByStatus(CertificateStatus status);
}
