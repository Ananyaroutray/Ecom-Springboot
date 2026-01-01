package com.java.ecom.mapper;

import com.java.ecom.dto.request.UserRequestDto;
import com.java.ecom.dto.response.UserResponseDto;
import com.java.ecom.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.control.MappingControl;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address",ignore = true)
    User toEntity(UserRequestDto dto);
    UserResponseDto toResponseDto(User user);
    @Mapping(target ="address", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User user);


}
