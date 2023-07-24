package com.group2.server.controller;

import com.group2.server.dto.UserDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String message;
    private UserDto user;
    private String jwt;
}
