package com.group2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.UserRepository;
import com.group2.server.services.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> loginUser(@RequestBody UserDto userDto) {
        try {
            String username = userDto.getUsername();
            String password = userDto.getPassword();

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = tokenService.generateJwt(auth);

            ApplicationUser user = userRepository.findByUsername(username).get();

            return new ResponseEntity<>(new AuthResponseDto("Login successful",
                    new UserDto(user.getId(), user.getUsername(), null,
                            UserDto.applicationUserRolesToDtoRoles(user.getAuthorities()), user.getFullName()),
                    token), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new AuthResponseDto("Authentication failed", null, null),
                    HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<UserDto> getUserInfo(Authentication authentication) {
        try {
            String username = authentication.getName();
            ApplicationUser user = userRepository.findByUsername(username).get();
            return new ResponseEntity<>(
                    new UserDto(null, user.getUsername(), null,
                            UserDto.applicationUserRolesToDtoRoles(user.getAuthorities()), user.getFullName()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/logout")
    public ResponseEntity<AuthResponseDto> logoutUser(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwtToken", "");
        jwtCookie.setMaxAge(0);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setSecure(true);
        response.addCookie(jwtCookie);

        return new ResponseEntity<>(new AuthResponseDto("Logout successful", null, null), HttpStatus.OK);
    }

}
