package com.java.ecom.exception.dto;

import lombok.Data;

@Data
public class ErrorDetails {

    private String  status;
    private String message;
    private String description;


}
