package com.group2.server.controller;

import com.group2.server.model.ApplicationUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private ApplicationUser user;
    private String jwt;

}
