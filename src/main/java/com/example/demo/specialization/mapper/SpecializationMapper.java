package com.example.demo.specialization.mapper;

import com.example.demo.specialization.SpecializationEntity;
import com.example.demo.specialization.model.SpecializationView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SpecializationMapper {
    SpecializationMapper INSTANCE = Mappers.getMapper(SpecializationMapper.class);

    SpecializationView toView(SpecializationEntity entity);
    List<SpecializationView> toViewList(Iterable<SpecializationEntity> entities);
    // No toEntity needed if RequestSpecialization DTO is used for create/update
}
