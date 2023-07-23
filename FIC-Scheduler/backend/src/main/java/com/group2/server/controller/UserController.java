package com.group2.server.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<UserDto[]> readUsersByQuery(@RequestParam(required = false) String username) {
        try {
            List<ApplicationUser> users;

            if (username != null) {
                users = new ArrayList<ApplicationUser>(1);
                var result = userRepository.findByUsername(username);
                if (result.isPresent()) {
                    users.add(result.get());
                }
            } else {
                users = userRepository.findAll();
            }

            var userDtos = new UserDto[users.size()];
            for (int i = 0; i < users.size(); ++i) {
                userDtos[i] = applicationUserAsDto(users.get(i));
            }
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> readUserById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<UserDto>(applicationUserAsDto(userRepository.findById(id).orElseThrow()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<UserDto>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<List<UserDto>> updateUsers(@RequestBody List<UserDto> userDtoList) {
        try {
            var updatedUsers = new ArrayList<UserDto>(userDtoList.size());
            for (var userDto : userDtoList) {
                updatedUsers.add(updateOrCreateUser(userDto));
            }
            return new ResponseEntity<>(updatedUsers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUserById(@PathVariable Integer id, @RequestBody UserDto userDto) {
        try {
            userDto.setId(id);
            UserDto createdUserDto = updateOrCreateUser(userDto);
            return new ResponseEntity<>(createdUserDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Integer id) {
        try {
            userRepository.delete(userRepository.findById(id).get());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private UserDto updateOrCreateUser(UserDto userDto) {
        String encodedPassword = null;
        if (userDto.getPassword() != null) {
            encodedPassword = passwordEncoder.encode(userDto.getPassword());
        }

        var authorities = new HashSet<Role>();
        if (userDto.getRoles() != null) {
            for (String roleStr : userDto.getRoles()) {
                authorities.add(Role.valueOf(roleStr));
            }
        } else {
            authorities.add(Role.COORDINATOR);
        }

        ApplicationUser createdUser;
        if (userDto.getId() != null) {
            createdUser = userRepository.findById(userDto.getId()).get();
            if (userDto.getUsername() != null) {
                createdUser.setUsername(userDto.getUsername());
            }
            if (encodedPassword != null) {
                createdUser.setPassword(encodedPassword);
            }
            if (userDto.getRoles() != null) {
                createdUser.setAuthorities(authorities);
            }
            if (userDto.getFullName() != null) {
                createdUser.setFullName(userDto.getFullName());
            }
            createdUser = userRepository.save(createdUser);
        } else {
            createdUser = userRepository
                    .save(new ApplicationUser(null, userDto.getUsername(), encodedPassword, authorities,
                            userDto.getFullName()));
        }
        return applicationUserAsDto(createdUser);
    }

    private UserDto applicationUserAsDto(ApplicationUser user) {
        return new UserDto(user.getId(), user.getUsername(), null,
                UserDto.applicationUserRolesToDtoRoles(user.getAuthorities()),
                user.getFullName());
    }

}
