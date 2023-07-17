package com.group2.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
import com.group2.server.model.Role;
import com.group2.server.repository.UserRepository;
import com.group2.server.services.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @PostMapping("/register")
    public UserInfoDto registerUser(@RequestBody RegistrationDto body) {
        // Build up the DB user object
        String encodedPassword = passwordEncoder.encode(body.getPassword());
        var authorities = new HashSet<Role>();
        authorities.add(Role.INSTRUCTOR);

        ApplicationUser user = userRepository
                .save(new ApplicationUser(null, body.getUsername(), encodedPassword, authorities, body.getUsername()));

        return new UserInfoDto(user.getUsername(), applicationUserRolesToDtoRoles(user), user.getFullName());
    }

    @PostMapping("/login")
    public LoginResponseDto loginUser(@RequestBody RegistrationDto body, HttpServletResponse response) {
        try {
            String username = body.getUsername();
            String password = body.getPassword();

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            String token = tokenService.generateJWT(auth);

            // Create a cookie
            Cookie jwtCookie = new Cookie("jwtToken", token);

            // Set the cookie properties
            jwtCookie.setMaxAge(7 * 24 * 60 * 60); // sets expire time for 7 days
            // jwtCookie.setSecure(true); // ensures the cookie is only sent over HTTPS
            jwtCookie.setHttpOnly(true); // protects against XSS attacks
            jwtCookie.setPath("/"); // allows the cookie to be sent with all requests

            // Add the cookie to the response
            response.addCookie(jwtCookie);

            Optional<ApplicationUser> user = userRepository.findByUsername(username);

            if (user.isPresent()) {
                return new LoginResponseDto("",
                        new UserInfoDto(user.get().getUsername(), applicationUserRolesToDtoRoles(user.get()),
                                user.get().getFullName()),
                        token);
            }
            // otherwise, fall through to error handling
        } catch (AuthenticationException e) {
            // fall through to error handling
        }
        response.setStatus(401);
        return new LoginResponseDto("Authentication failed", null, null);
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

    private ArrayList<String> applicationUserRolesToDtoRoles(ApplicationUser user) {
        // Convert the DB object into a DTO, presuming our save was successful
        var strRoles = new ArrayList<String>();
        for (var role : user.getAuthorities()) {
            strRoles.add(role.getAuthority());
        }
        return strRoles;
    }

}
