package com.example.demo.certificate;

// import com.example.demo.certificate.minio.MinioService; // MinioService is used by CertificateServiceImpl
import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateView;
import com.example.demo.certificate.role.CertificateStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController implements CertificateControllerApi {

    private final CertificateService certificateService;
    // private final MinioService minioService; // Injected into CertificateServiceImpl

    @Override
    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasRole('DOCTOR')") // Assuming only doctors can upload their certificates
    public ResponseEntity<String> uploadCertificateFile(@RequestPart("file") MultipartFile file) {
        String fileUrl = certificateService.uploadCertificateFile(file);
        return ResponseEntity.ok(fileUrl);
    }

    @Override
    @PostMapping()
    @PreAuthorize("hasRole('DOCTOR')") // Assuming only doctors can add their certificate details
    public ResponseEntity<CertificateView> addCertificate(Principal principal, @RequestBody CertificateRequest request) {
        CertificateView certificateView = certificateService.addCertificate(principal, request);
        return new ResponseEntity<>(certificateView, HttpStatus.CREATED);
    }

    @Override
    @GetMapping()
    @PreAuthorize("hasRole('DOCTOR')") // Doctors can get their own certificates
    public ResponseEntity<List<CertificateView>> getMyCertificates(Principal principal) {
        List<CertificateView> certificates = certificateService.getMyCertificates(principal);
        return ResponseEntity.ok(certificates);
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')") // Only Admin can update status
    public ResponseEntity<String> updateCertificateStatus(@PathVariable Long id, @RequestParam CertificateStatus status) {
        String message = certificateService.updateCertificateStatus(id, status);
        return ResponseEntity.ok(message);
    }

    @Override
    @GetMapping("/{doctorId}")
    // No PreAuthorize here, assuming public or handled by service if specific roles needed (e.g. Admin for any doctor)
    public ResponseEntity<List<CertificateView>> getDoctorCertificatesById(@PathVariable Long doctorId) {
        List<CertificateView> certificates = certificateService.getDoctorAllCertificatesByDoctorId(doctorId);
        return ResponseEntity.ok(certificates);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')") // Doctor can delete their own, Admin can delete any (logic in service)
    public ResponseEntity<String> deleteCertificate(Principal principal, @PathVariable Long id) {
        String message = certificateService.deleteCertificate(principal, id);
        return ResponseEntity.ok(message);
    }

    @Override
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')") // Only Admin can query by status
    public ResponseEntity<List<CertificateView>> getCertificatesByStatus(@RequestParam CertificateStatus status) {
        List<CertificateView> certificates = certificateService.getCertificatesByStatus(status);
        return ResponseEntity.ok(certificates);
    }
}
