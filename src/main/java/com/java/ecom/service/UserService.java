package com.java.ecom.service;



import com.java.ecom.dto.request.UserRequestDto;
import com.java.ecom.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {

    List<UserResponseDto> getAllUsers();
    UserResponseDto addUsers(UserRequestDto dto);
    UserResponseDto updateUser(int id, UserRequestDto dto);
    void deleteUser(int id);

}
