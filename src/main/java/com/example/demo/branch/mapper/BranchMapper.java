package com.example.demo.branch.mapper;

import com.example.demo.branch.BranchEntity;
import com.example.demo.branch.model.BranchView;
import com.example.demo.disease.mapper.DiseaseMapper; // Import DiseaseMapper
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = DiseaseMapper.class) // This will be added when DiseaseMapper is created
public interface BranchMapper {
    BranchMapper INSTANCE = Mappers.getMapper(BranchMapper.class);

    @Mapping(source = "id", target = "branchId")
    @Mapping(source = "doctorEntity.id", target = "doctorId")
    @Mapping(source = "diseases", target = "diseases") // Map diseases field
    BranchView toView(BranchEntity entity);

    @Mapping(source = "branchId", target = "id")
    @Mapping(source = "doctorId", target = "doctorEntity.id")
    @Mapping(source = "diseases", target = "diseases") // Map diseases field
    BranchEntity toEntity(BranchView view);
}
