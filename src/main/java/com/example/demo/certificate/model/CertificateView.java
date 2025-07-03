package com.example.demo.certificate.model;

import com.example.demo.certificate.role.CertificateStatus;
import lombok.Builder;
import lombok.Data;

// Simplified to match existing CertificateResponse and mapToResponse logic
@Data
@Builder
public class CertificateView {
    private Long id;
    private Long doctorId;
    // private String doctorFullName; // Not in original mapToResponse, can be added later if needed
    private String title; // Corresponds to CertificateEntity.title / CertificateRequest.title
    private String fileUrl;
    private CertificateStatus status;
    // private String description; // Covered by title
    // private LocalDateTime uploadedAt; // Not in original mapToResponse
    // private LocalDateTime approvedAt; // Not in original mapToResponse
    // private LocalDateTime rejectedAt; // Not in original mapToResponse
    // private String adminComment; // Not in original mapToResponse
}
