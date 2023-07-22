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
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public UserDto[] readUsersByQuery(@RequestParam Optional<String> username) {
        List<ApplicationUser> users;

        if (username.isPresent()) {
            users = new ArrayList<ApplicationUser>(1);
            var result = userRepository.findByUsername(username.get());
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
        userDto.setId(Optional.of(id));
        return updateOrCreateUser(userDto);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        userRepository.delete(userRepository.findById(id).get());
    }

    private UserDto updateOrCreateUser(UserDto userDto) {
        Optional<String> encodedPassword = Optional.empty();
        if (userDto.getPassword().isPresent()) {
            encodedPassword = Optional.of(passwordEncoder.encode(userDto.getPassword().get()));
        }

        var authorities = new HashSet<Role>();
        if (userDto.getRoles().isPresent()) {
            for (String roleStr : userDto.getRoles().get()) {
                authorities.add(Role.valueOf(roleStr));
            }
        } else {
            authorities.add(Role.COORDINATOR);
        }

        ApplicationUser createdUser;
        if (userDto.getId().isPresent()) {
            createdUser = userRepository.findById(userDto.getId().get()).get();
            if (userDto.getUsername().isPresent()) {
                createdUser.setUsername(userDto.getUsername().get());
            }
            if (encodedPassword.isPresent()) {
                createdUser.setPassword(encodedPassword.get());
            }
            if (userDto.getRoles().isPresent()) {
                createdUser.setAuthorities(authorities);
            }
            if (userDto.getFullName().isPresent()) {
                createdUser.setFullName(userDto.getFullName().get());
            }
        } else {
            createdUser = userRepository
                    .save(new ApplicationUser(null, userDto.getUsername().get(), encodedPassword.get(), authorities,
                            userDto.getFullName().get()));
        }
        return applicationUserAsDto(createdUser);
    }

    private UserDto applicationUserAsDto(ApplicationUser user) {
        return new UserDto(Optional.of(user.getId()), Optional.of(user.getUsername()), Optional.empty(),
                Optional.of(UserDto.applicationUserRolesToDtoRoles(user.getAuthorities())),
                Optional.of(user.getFullName()));
    }

}
