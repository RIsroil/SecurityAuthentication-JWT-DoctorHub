package com.example.demo.certificate;

import com.example.demo.certificate.minio.MinioService;
import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.role.CertificateStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/certificate")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final MinioService minioService;

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping(path = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = minioService.uploadFile(file);
            return ResponseEntity.ok().body("{\"videoUrl\": \"" + fileUrl + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Faylni yuklashda xatolik: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping()
    public ResponseEntity<?> addCertificate(@RequestParam String accessToken, @RequestBody CertificateRequest request) {
        return ResponseEntity.ok(certificateService.addCertificate(accessToken, request));
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping()
    public ResponseEntity<?> getCertificates(@RequestParam String accessToken) {
        return ResponseEntity.ok(certificateService.getMyCertificates(accessToken));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam CertificateStatus status){
        return ResponseEntity.ok(certificateService.updateStatus(id, status));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR','PATIENT')")
    @GetMapping("/{doctorId}")
    public ResponseEntity<?> getDoctorCertificatesById(@PathVariable Long doctorId){
        return ResponseEntity.ok(certificateService.getDoctorAllCertificatesByDoctorId(doctorId));
    }

    @PreAuthorize("hasAnyRole('ADMIN','DOCTOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@RequestParam String accessToken, @PathVariable Long id){
        return ResponseEntity.ok(certificateService.deleteCertificate(accessToken, id));
    }
}
