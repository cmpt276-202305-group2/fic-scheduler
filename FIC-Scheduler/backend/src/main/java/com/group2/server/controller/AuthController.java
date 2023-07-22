package com.group2.server.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.*;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.UserRepository;
import com.group2.server.services.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    public AuthResponseDto loginUser(@RequestBody UserDto body, HttpServletResponse response) {
        try {
            String username = body.getUsername().get();
            String password = body.getPassword().get();

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = tokenService.generateJWT(auth);

            Optional<ApplicationUser> user = userRepository.findByUsername(username);

            if (user.isPresent()) {
                return new AuthResponseDto("Login successful",
                        new UserDto(Optional.of(user.get().getId()), Optional.of(user.get().getUsername()),
                                Optional.empty(),
                                Optional.of(applicationUserRolesToDtoRoles(user.get())),
                                Optional.of(user.get().getFullName())),
                        token);
            }

        } catch (AuthenticationException e) {
            response.setStatus(401);
            return null;
        }
        return new AuthResponseDto("Authentication failed", null, null);
    }

    @GetMapping("/current-user")
    public UserDto getUserInfo(Authentication authentication) {
        String username = authentication.getName();
        Optional<ApplicationUser> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            throw new JwtException("Bad username in JWT");
        }
        return new UserDto(Optional.empty(), Optional.of(user.get().getUsername()), Optional.empty(),
                Optional.of(applicationUserRolesToDtoRoles(user.get())),
                Optional.of(user.get().getFullName()));

    }

    @PostMapping("/logout")
    public AuthResponseDto logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {

                    Cookie jwtCookie = new Cookie("jwtToken", "");
                    jwtCookie.setMaxAge(0);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");
                    jwtCookie.setSecure(true);

                    response.addCookie(jwtCookie);
                }
            }
        }
        return new AuthResponseDto("Logout successful", null, null);
    }

    private ArrayList<String> applicationUserRolesToDtoRoles(ApplicationUser user) {
        var strRoles = new ArrayList<String>();
        for (var role : user.getAuthorities()) {
            strRoles.add(role.getAuthority());
        }
        return strRoles;
    }

}
