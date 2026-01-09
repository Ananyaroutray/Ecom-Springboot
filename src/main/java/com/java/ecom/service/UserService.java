package com.java.ecom.service;



import com.java.ecom.dto.request.UserRequestDto;
import com.java.ecom.dto.response.UserResponseDto;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto addUsers(UserRequestDto dto);
    UserResponseDto updateUser(UUID id, UserRequestDto dto);
    void deleteUser(UUID id);

}
