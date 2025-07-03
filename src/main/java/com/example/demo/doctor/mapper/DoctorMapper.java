package com.example.demo.doctor.mapper;

import com.example.demo.doctor.DoctorEntity;
import com.example.demo.doctor.model.DoctorView;
import com.example.demo.address.mapper.AddressMapper;
import com.example.demo.certificate.mapper.CertificateMapper;
import com.example.demo.specialization.SpecializationEntity;
import com.example.demo.specialization.mapper.SpecializationMapper; // For later

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(uses = {AddressMapper.class, CertificateMapper.class, SpecializationMapper.class })
public interface DoctorMapper {
    DoctorMapper INSTANCE = Mappers.getMapper(DoctorMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "address", target = "address") // Uses AddressMapper
    @Mapping(source = "specializationIds", target = "specializations") // Uses SpecializationMapper
    @Mapping(source = "certificates", target = "certificates") // Uses CertificateMapper
    DoctorView toView(DoctorEntity entity);

    // No toEntity needed for now as registration uses DoctorRegisterRequestDTO

    // The mapSpecializationsToNames method is no longer needed as SpecializationMapper handles the conversion
    // from List<SpecializationEntity> to List<SpecializationView>
    // @Named("mapSpecializationsToNames")
    // default List<String> mapSpecializationsToNames(List<SpecializationEntity> specializations) {
    //     if (specializations == null) {
    //         return null;
    //     }
    //     return specializations.stream()
    //             .map(SpecializationEntity::getSpecializationName) // Corrected to getSpecializationName
    //             .collect(Collectors.toList());
    // }
}
