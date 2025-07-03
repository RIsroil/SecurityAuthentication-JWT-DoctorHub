package com.example.demo.patient.mapper;

import com.example.demo.patient.PatientEntity;
import com.example.demo.patient.model.PatientView;
import com.example.demo.address.mapper.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {AddressMapper.class})
public interface PatientMapper {
    PatientMapper INSTANCE = Mappers.getMapper(PatientMapper.class);

    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "address", target = "address") // Uses AddressMapper
    PatientView toView(PatientEntity entity);

    // No toEntity needed for now as registration uses PatientRegisterRequestDTO
}
