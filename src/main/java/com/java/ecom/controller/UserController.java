package com.java.ecom.controller;

import com.java.ecom.dto.request.UserRequestDto;
import com.java.ecom.dto.response.UserResponseDto;
import com.java.ecom.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> addUsers(@Valid @RequestBody UserRequestDto dto){
        return new ResponseEntity<>(userService.addUsers(dto),HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUsers(@PathVariable int id, @Valid @RequestBody UserRequestDto dto){
        return new ResponseEntity<>(userService.updateUser(id,dto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id){
        userService.deleteUser(id);
        return new ResponseEntity<>("User Deleted", HttpStatus.OK);
    }

}
