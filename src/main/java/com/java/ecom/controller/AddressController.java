package com.java.ecom.controller;

import com.java.ecom.dto.request.AddressRequestDto;
import com.java.ecom.dto.response.AddressResponseDto;
import com.java.ecom.entity.Address;
import com.java.ecom.service.AddressService;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddressByUser(@PathVariable UUID userId){
        return new ResponseEntity<>(addressService.getAddressesByUser(userId),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(@PathVariable UUID userId, @RequestBody AddressRequestDto dto){
        return new ResponseEntity<>(addressService.addAddress(userId,dto), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AddressResponseDto> updateAddress(@PathVariable UUID uId, @PathVariable Integer aId, @RequestBody AddressRequestDto dto){
        return new ResponseEntity<>(addressService.updateAddress(uId,aId,dto), HttpStatus.OK);
    }

    @DeleteMapping("/{addressId}")
    public void deleteAddress(@PathVariable Long addressId, @PathVariable UUID userId){
        addressService.deleteAddress(userId,addressId);
    }

}
