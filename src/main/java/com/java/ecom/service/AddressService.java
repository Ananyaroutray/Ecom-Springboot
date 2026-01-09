package com.java.ecom.service;

import com.java.ecom.dto.request.AddressRequestDto;
import com.java.ecom.dto.response.AddressResponseDto;
import com.java.ecom.entity.Address;

import java.util.List;
import java.util.UUID;

public interface AddressService {


     List<AddressResponseDto> getAddressesByUser(UUID userId);
    AddressResponseDto addAddress(UUID userId, AddressRequestDto dto);
    AddressResponseDto updateAddress(UUID userId, Integer addressId, AddressRequestDto dto);
    void deleteAddress(UUID userId, Long addressId);

}
