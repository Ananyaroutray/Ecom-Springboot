package com.java.ecom.dto.response;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserResponseDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
    private List<AddressResponseDto> address;
}
