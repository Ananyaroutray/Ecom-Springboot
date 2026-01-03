package com.java.ecom.mapper;

import com.java.ecom.dto.request.ProductRequestDto;
import com.java.ecom.dto.response.ProductResponseDto;
import com.java.ecom.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "isAvailable", ignore = true)
    Product toEntity(ProductRequestDto dto);
    ProductResponseDto toResponseDto(Product product);
    void toUpdateEntityFromDto(ProductRequestDto dto, @MappingTarget Product product);
}
