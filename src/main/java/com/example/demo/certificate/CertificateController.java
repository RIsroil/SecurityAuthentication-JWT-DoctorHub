package com.example.demo.certificate;

import com.example.demo.certificate.minio.MinioService;
import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateResponse;
import com.example.demo.certificate.role.CertificateStatus;
import lombok.RequiredArgsConstructor;
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
public class CertificateController {

    private final CertificateService certificateService;
    private final MinioService minioService;

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadCertificate(@RequestParam("file") MultipartFile file) {
            return ResponseEntity.ok(minioService.uploadCertificate(file));
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @PostMapping()
    public CertificateResponse addCertificate(Principal principal, @RequestBody CertificateRequest request) {
        return certificateService.addCertificate(principal, request);
    }

    @PreAuthorize("hasAnyRole('DOCTOR','ADMIN')")
    @GetMapping()
    public List<CertificateResponse> getCertificates(Principal principal) {
        return certificateService.getMyCertificates(principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam CertificateStatus status){
        return ResponseEntity.ok(certificateService.updateStatus(id, status));
    }

    @GetMapping("/{doctorId}")
    public List<CertificateResponse> getDoctorCertificatesById(@PathVariable Long doctorId){
        return certificateService.getDoctorAllCertificatesByDoctorId(doctorId);
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(Principal principal, @PathVariable Long id){
        return ResponseEntity.ok(certificateService.deleteCertificate(principal, id));
    }
}
