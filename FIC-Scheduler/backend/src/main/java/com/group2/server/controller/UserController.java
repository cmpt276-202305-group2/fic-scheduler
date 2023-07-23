package com.group2.server.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
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
    public UserDto[] readUsersByQuery(@RequestParam(required = false) String username) {
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
        return userDtos;
    }

    @GetMapping("/users/{id}")
    public UserDto readUserById(@PathVariable Integer id) {
        if (id == null) {
            return null;
        }
        return applicationUserAsDto(userRepository.findById(id).orElseThrow());
    }

    @PostMapping("/users")
    public List<UserDto> updateUsers(@RequestBody List<UserDto> userDtoList) {
        var updatedUsers = new ArrayList<UserDto>(userDtoList.size());
        for (var userDto : userDtoList) {
            updatedUsers.add(updateOrCreateUser(userDto));
        }
        return updatedUsers;
    }

    @PutMapping("/users/{id}")
    public UserDto updateUserById(@PathVariable Integer id, @RequestBody UserDto userDto) {
        userDto.setId(id);
        return updateOrCreateUser(userDto);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        userRepository.delete(userRepository.findById(id).get());
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
