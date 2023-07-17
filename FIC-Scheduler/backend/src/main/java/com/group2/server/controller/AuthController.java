package com.group2.server.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.group2.server.config.SecurityConfiguration;
import com.group2.server.model.ApplicationUser;
import com.group2.server.services.AuthService;
import org.springframework.security.oauth2.jwt.Jwt;
// import org.springframework.security.oauth2.jwt.JwtException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @PostMapping("/register")
    public ApplicationUser registerUser(@RequestBody RegistrationDto body) {
        return authService.registerUser(body.getUsername(), body.getPassword());
    }

    @PostMapping("/login")
    public String loginUser(@RequestBody RegistrationDto body, HttpServletResponse response) {
        LoginResponseDto loginResponseDto = authService.loginUser(body.getUsername(), body.getPassword());
    
        // Create a cookie
        Cookie jwtCookie = new Cookie("jwtToken", loginResponseDto.getJwt());

        // Set the cookie properties
        jwtCookie.setMaxAge(7 * 24 * 60 * 60); // sets expire time for 7 days
        // jwtCookie.setSecure(true); // ensures the cookie is only sent over HTTPS
        jwtCookie.setHttpOnly(true); // protects against XSS attacks
        jwtCookie.setPath("/"); // allows the cookie to be sent with all requests

        // Add the cookie to the response
        response.addCookie(jwtCookie);
    
        return "login successful";
}

    @GetMapping("/userinfo")
    public UserInfoDto getUserInfo(HttpServletRequest request) {
        // Extract the JWT token from the request cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    String token = cookie.getValue();
                    
                    // Decode the token and get the user's roles
                    try {
                        Jwt jwt = securityConfiguration.jwtDecoder().decode(token);
                        List<String> roles = jwt.getClaimAsStringList("roles");

                        // Create the response DTO and return it
                        UserInfoDto dto = new UserInfoDto();
                        dto.setRoles(roles);
                        return dto;
                    } catch (JwtException ex) {
                        // Invalid token
                        throw new RuntimeException("Invalid JWT token", ex);
                    }
                }
            }
        }

        throw new RuntimeException("User is not authenticated");
    }

    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request, HttpServletResponse response) {
        // Extract the JWT token from the request cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwtToken")) {
                    // Invalidate the cookie by setting its max age to 0
                    Cookie jwtCookie = new Cookie("jwtToken", "");
                    jwtCookie.setMaxAge(0);
                    jwtCookie.setHttpOnly(true);
                    jwtCookie.setPath("/");

                    // Add the cookie to the response
                    response.addCookie(jwtCookie);

                    return "logout successful";
                }
            }
        }

        throw new RuntimeException("User is not authenticated");
    }


}
