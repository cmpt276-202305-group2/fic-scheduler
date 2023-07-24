package com.group2.server.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String message;
    private UserDto user;
    private String jwt;
}
