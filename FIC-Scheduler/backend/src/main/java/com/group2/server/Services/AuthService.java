package com.group2.server.Services;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group2.server.Model.ApplicationUser;
import com.group2.server.Model.LoginResponeDTO;
import com.group2.server.Model.Role;
import com.group2.server.Repository.RoleRepository;
import com.group2.server.Repository.UserRepository;

@Service
@Transactional
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ApplicationUser registerUser(String username, String password){
        
        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("INSTRUCTOR").get();

        Set<Role> authorities = new HashSet<>();

        authorities.add(userRole);

        return userRepository.save(new ApplicationUser(0, username, encodedPassword, authorities));
    }

    public LoginResponeDTO loginUser(String username, String password){
        try{
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
                );   

                String token = tokenService.generateJWT(auth);

                return new LoginResponeDTO(userRepository.findByUsername(username).get(), token);
        }
        catch(AuthenticationException e){
            return new LoginResponeDTO(null, "");
        }
           
    }

}
