package com.example.demo.address.mapper;

import com.example.demo.address.AddressEntity;
import com.example.demo.address.model.AddressView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AddressMapper {
    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    AddressView toView(AddressEntity entity);
    AddressEntity toEntity(AddressView view);
}
