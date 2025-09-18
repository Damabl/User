package com.example.demo.mapper;

import com.example.demo.dto.CreateUserDto;
import com.example.demo.dto.UserDto;
import com.example.demo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDto toDto(User user);
    
    @Mapping(target = "id", ignore = true)
    User toEntity(CreateUserDto createUserDto);
    
    @Mapping(target = "id", ignore = true)
    void updateEntity(CreateUserDto createUserDto, @org.mapstruct.MappingTarget User user);
}



