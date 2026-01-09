package com.java.ecom.service.Implementation;

import com.java.ecom.dto.request.UserRequestDto;
import com.java.ecom.dto.response.UserResponseDto;
import com.java.ecom.entity.User;
import com.java.ecom.exception.DuplicateResourceException;
import com.java.ecom.exception.NotFoundException;
import com.java.ecom.mapper.UserMapper;
import com.java.ecom.repository.UserRepo;
import com.java.ecom.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepo.findAll().stream().map(userMapper::toResponseDto).toList();
    }

    @Override
    public UserResponseDto addUsers(UserRequestDto dto) {

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + dto.getEmail()
            );
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setRole("USER");

        User savedUser = userRepo.save(newUser);
        return userMapper.toResponseDto(savedUser);
    }


    @Override
    public UserResponseDto updateUser(UUID id, UserRequestDto dto) {
        User existingUser = userRepo.findById(id).orElseThrow(()-> new NotFoundException("User Not found for id: "+ id));
        userMapper.updateEntityFromDto(dto,existingUser);
        return userMapper.toResponseDto(existingUser);
    }

    @Override
    public void deleteUser(UUID id) {
        User user = userRepo.findById(id).orElseThrow(()->new NotFoundException("User Not found for id: "+ id));
        userRepo.delete(user);
    }
}
