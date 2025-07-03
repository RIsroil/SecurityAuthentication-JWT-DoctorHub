package com.example.demo.certificate.mapper;

import com.example.demo.certificate.CertificateEntity;
import com.example.demo.certificate.model.CertificateView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CertificateMapper {
    CertificateMapper INSTANCE = Mappers.getMapper(CertificateMapper.class);

    @Mapping(source = "doctor.id", target = "doctorId")
    // title in CertificateEntity is mapped to title in CertificateView
    // fileUrl in CertificateEntity is mapped to fileUrl in CertificateView
    // status in CertificateEntity is mapped to status in CertificateView
    CertificateView toView(CertificateEntity entity);

    // This mapping might be needed if we decide to create/update entities from CertificateView directly.
    // For now, CertificateRequest is used for creation.
    @Mapping(source = "doctorId", target = "doctor.id")
    CertificateEntity toEntity(CertificateView view);
}
