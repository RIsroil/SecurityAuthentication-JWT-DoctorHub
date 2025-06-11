package com.example.demo.certificate;

import com.example.demo.certificate.minio.MinioService;
import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateResponse;
import com.example.demo.certificate.role.CertificateStatus;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final DoctorRepository doctorRepository;
    private final MinioService minioService;
    private final AuthHelperService authHelperService;

    public CertificateResponse addCertificate(String accessToken, CertificateRequest dto) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);;
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        CertificateEntity cert = new CertificateEntity();
        cert.setTitle(dto.getTitle());
        cert.setFileUrl(dto.getFileUrl());
        cert.setStatus(CertificateStatus.PENDING);
        cert.setDoctor(doctor);
        certificateRepository.save(cert);

        return CertificateResponse.builder()
                .id(cert.getId())
                .title(cert.getTitle())
                .fileUrl(cert.getFileUrl())
                .status(cert.getStatus())
                .build();
    }

    public ResponseEntity<?> updateStatus(Long id, CertificateStatus status) {
        CertificateEntity cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        cert.setStatus(status);
        certificateRepository.save(cert);

        DoctorEntity doctor = cert.getDoctor();
        long verifiedCount = doctor.getCertificates().stream()
                .filter(c -> c.getStatus() == CertificateStatus.VERIFIED)
                .count();
        doctor.setVerified(verifiedCount > 0);
        doctorRepository.save(doctor);

        return ResponseEntity.ok("Status muvaffaqiyatli yangilandi");
    }

    public List<CertificateResponse> getMyCertificates(String accessToken) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctor.getId());

        return certEntities.stream()
                .map(cert -> CertificateResponse.builder()
                        .id(cert.getId())
                        .title(cert.getTitle())
                        .fileUrl(cert.getFileUrl())
                        .status(cert.getStatus())
                        .build())
                .toList();
    }

    public List<CertificateResponse> getDoctorAllCertificatesByDoctorId(Long doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctorId);
        return certEntities.stream()
                .map(cert -> CertificateResponse.builder()
                    .id(cert.getId())
                    .title(cert.getTitle())
                    .fileUrl(cert.getFileUrl())
                    .status(cert.getStatus())
                    .build())
                .toList();
    }

    public ResponseEntity<?> deleteCertificate(String accessToken, Long id) {
        UserEntity user = authHelperService.getUserFromToken(accessToken);;
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        CertificateEntity cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getDoctor().getId().equals(doctor.getId()) || user.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can not delete this certificate");
        }

        try {
            minioService.deleteFile(cert.getFileUrl());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("MinIO faylini o'chirishda xatolik: " + ex.getMessage());
        }
        certificateRepository.delete(cert);

        long verifiedCount = doctor.getCertificates().stream()
                .filter(c -> !c.getId().equals(id))
                .filter(c -> c.getStatus() == CertificateStatus.VERIFIED)
                .count();

        doctor.setVerified(verifiedCount > 0);
        doctorRepository.save(doctor);

        return ResponseEntity.ok("Certificate muvaffaqiyatli o'chirildi");
    }

}
