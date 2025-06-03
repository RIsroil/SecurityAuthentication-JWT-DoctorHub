package com.example.demo.certificate.model;

import com.example.demo.certificate.role.CertificateStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CertificateResponse {
    private Long id;
    private String title;
    private String fileUrl;
    private CertificateStatus status;

}

