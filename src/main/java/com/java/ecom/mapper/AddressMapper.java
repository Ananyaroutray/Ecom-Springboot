package com.java.ecom.mapper;

import com.java.ecom.dto.request.AddressRequestDto;
import com.java.ecom.dto.response.AddressResponseDto;
import com.java.ecom.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressRequestDto dto);
    AddressResponseDto toResponseDto(Address address);

    // @MappingTarget, Without it: MapStruct creates a new object, With it: MapStruct fills existing object
    void updateEntityFromDto(AddressRequestDto dto, @MappingTarget Address address);

}
