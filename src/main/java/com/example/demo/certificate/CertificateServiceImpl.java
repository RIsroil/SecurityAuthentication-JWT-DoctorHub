package com.example.demo.certificate;

import com.example.demo.certificate.mapper.CertificateMapper;
import com.example.demo.certificate.minio.MinioService;
import com.example.demo.certificate.model.CertificateRequest;
import com.example.demo.certificate.model.CertificateView;
import com.example.demo.certificate.role.CertificateStatus;
import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.DoctorRepository;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.user.Role;
import com.example.demo.user.UserEntity;
import com.example.demo.user.auth.AuthHelperService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final DoctorRepository doctorRepository;
    private final MinioService minioService;
    private final AuthHelperService authHelperService;
    private final CertificateMapper certificateMapper = CertificateMapper.INSTANCE;

    @Override
    public String uploadCertificateFile(MultipartFile file) {
        // Assuming MinioService's uploadCertificate directly returns the URL or throws an exception
        return minioService.uploadCertificate(file);
    }

    @Override
    @Transactional
    public CertificateView addCertificate(Principal principal, CertificateRequest request) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        // Ensure user is a doctor or has appropriate role if necessary
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor profile not found for user: " + user.getUsername());
        }

        CertificateEntity cert = new CertificateEntity();
        cert.setTitle(request.getTitle()); // title from request maps to title in entity
        cert.setFileUrl(request.getFileUrl());
        cert.setStatus(CertificateStatus.PENDING);
        cert.setDoctor(doctor);
        // Timestamps like uploadedAt could be set here if needed: cert.setUploadedAt(LocalDateTime.now());

        CertificateEntity savedCert = certificateRepository.save(cert);
        return certificateMapper.toView(savedCert);
    }

    @Override
    public List<CertificateView> getMyCertificates(Principal principal) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        DoctorEntity doctor = doctorRepository.findByUser_Id(user.getId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor profile not found for user: " + user.getUsername());
        }
        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctor.getId());
        return certEntities.stream()
                .map(certificateMapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String updateCertificateStatus(Long certificateId, CertificateStatus status) {
        CertificateEntity cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with ID: " + certificateId));

        cert.setStatus(status);
        // Potentially set approvedAt/rejectedAt timestamps here
        // if (status == CertificateStatus.VERIFIED) cert.setApprovedAt(LocalDateTime.now());
        // else if (status == CertificateStatus.REJECTED) cert.setRejectedAt(LocalDateTime.now());
        certificateRepository.save(cert);

        // Update doctor's verification status
        DoctorEntity doctor = cert.getDoctor();
        long verifiedCount = doctor.getCertificates().stream()
                .filter(c -> c.getStatus() == CertificateStatus.VERIFIED)
                .count();
        doctor.setVerified(verifiedCount > 0);
        doctorRepository.save(doctor);

        return "Status successfully updated for certificate ID: " + certificateId;
    }

    @Override
    public List<CertificateView> getDoctorAllCertificatesByDoctorId(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + doctorId);
        }
        List<CertificateEntity> certEntities = certificateRepository.findAllCertificatesByDoctorId(doctorId);
        return certEntities.stream()
                .map(certificateMapper::toView)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String deleteCertificate(Principal principal, Long certificateId) {
        UserEntity user = authHelperService.getUserFromPrincipal(principal);
        CertificateEntity cert = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found with ID: " + certificateId));

        DoctorEntity doctorOfCertificate = cert.getDoctor();

        // Check if the current user is the owner of the certificate or an ADMIN
        boolean isOwner = doctorOfCertificate.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("You are not authorized to delete this certificate.");
        }

        try {
            minioService.deleteFile(cert.getFileUrl());
        } catch (Exception ex) {
            // Log the exception and consider how to handle this - rethrow, return specific message, etc.
            // For now, let's allow deletion from DB even if MinIO fails, but this could be stricter.
            // logger.error("Error deleting file from MinIO: " + cert.getFileUrl(), ex);
            throw new RuntimeException("Error deleting file from storage: " + ex.getMessage(), ex);
        }

        certificateRepository.delete(cert);

        // Update doctor's verification status after deletion
        // Need to fetch the doctor again as the cert list might be stale if not managed by JPA correctly post-delete
        DoctorEntity doctorToUpdate = doctorRepository.findById(doctorOfCertificate.getId()).orElseThrow(
            () -> new ResourceNotFoundException("Doctor not found while updating verification status post-certificate deletion.")
        );

        long verifiedCount = doctorToUpdate.getCertificates().stream()
                                 .filter(c -> c.getStatus() == CertificateStatus.VERIFIED)
                                 .count(); //This count will reflect the list after deletion
        doctorToUpdate.setVerified(verifiedCount > 0);
        doctorRepository.save(doctorToUpdate);

        return "Certificate successfully deleted with ID: " + certificateId;
    }

    @Override
    public List<CertificateView> getCertificatesByStatus(CertificateStatus status) {
        return certificateRepository.findAllCertificatesByStatus(status).stream()
                .map(certificateMapper::toView)
                .collect(Collectors.toList());
    }
}
