package com.example.demo.certificate;

import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateResponse;
import com.example.demo.certificate.role.CertificateStatus;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.user.UserEntity;
import com.example.demo.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final CertificateRepository certificateRepository;
    private final DoctorRepository doctorRepository;

    public ResponseEntity<?> addCertificate(String accessToken, CertificateRequest dto) {
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri: " + e.getMessage());
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        CertificateEntity cert = new CertificateEntity();
        cert.setTitle(dto.getTitle());
        cert.setFileUrl(dto.getFileUrl());
        cert.setStatus(CertificateStatus.PENDING);
        cert.setDoctor(doctor);

        certificateRepository.save(cert);

        return ResponseEntity.ok("Certificat muvaffaqiyatli qo'shildi!");
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

    public ResponseEntity<?> getMyCertificates(String accessToken) {
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri: " + e.getMessage());
        }

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctor.getId());

        List<CertificateResponse> responses = certEntities.stream()
                .map(cert -> CertificateResponse.builder()
                        .id(cert.getId())
                        .title(cert.getTitle())
                        .fileUrl(cert.getFileUrl())
                        .status(cert.getStatus())
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<?> getDoctorAllCertificatesByDoctorId(Long doctorId) {
        DoctorEntity doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctorId);
        List<CertificateResponse> responses = certEntities.stream()
                .map(cert -> CertificateResponse.builder()
                        .id(cert.getId())
                        .title(cert.getTitle())
                        .fileUrl(cert.getFileUrl())
                        .status(cert.getStatus())
                        .build())
                .toList();

        return ResponseEntity.ok(responses);
    }

    public ResponseEntity<?> deleteCertificate(String accessToken, Long id) {
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token noto‘g‘ri: " + e.getMessage());
        }
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi"));

        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());

        CertificateEntity cert = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!cert.getDoctor().getId().equals(doctor.getId())) {
            throw new AccessDeniedException("You can not delete this certificate");
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
