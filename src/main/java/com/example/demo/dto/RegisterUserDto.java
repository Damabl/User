package com.example.demo.dto;

import lombok.Data;

@Data
public class RegisterUserDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
}


