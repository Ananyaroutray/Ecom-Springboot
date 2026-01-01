package com.java.ecom.service;

import com.java.ecom.dto.request.AddressRequestDto;
import com.java.ecom.dto.response.AddressResponseDto;
import com.java.ecom.entity.Address;

import java.util.List;

public interface AddressService {


     List<AddressResponseDto> getAddressesByUser(Integer userId);
    AddressResponseDto addAddress(Integer userId, AddressRequestDto dto);
    AddressResponseDto updateAddress(Integer userId, Integer addressId, AddressRequestDto dto);
    void deleteAddress(Integer userId, Long addressId);

}
