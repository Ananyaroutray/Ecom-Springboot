package com.java.ecom.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDto {

    private String firstName;
    private String lastName;

    @Size(min = 4, max = 10, message = "UserName must be between 4 and 10 characters")
    private String userName;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String passWord;

    @NotBlank(message = "email should not be blank")
    @Email(message = "email format should be followed")
    private String email;

    @NotBlank(message = "phone no should be added")
    @Size(min = 10, message = "Number should be 10 digit")
    private String phone;

}

