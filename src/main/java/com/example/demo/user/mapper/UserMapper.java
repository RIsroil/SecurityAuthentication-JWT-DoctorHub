package com.example.demo.user.mapper;

import com.example.demo.user.UserEntity;
import com.example.demo.user.model.UserView;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserView toView(UserEntity entity);
}
