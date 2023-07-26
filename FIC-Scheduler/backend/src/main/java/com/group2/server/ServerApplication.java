package com.group2.server;

import java.util.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group2.server.controller.*;
import com.group2.server.dto.*;
import com.group2.server.repository.*;

import lombok.*;

@SpringBootApplication()
@ConfigurationProperties("fic-scheduler")
public class ServerApplication {
    @Getter
    private final List<UserDto> defaultUsers = new ArrayList<>();

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, UserController userController,
            PasswordEncoder passwordEncoder) {
        return args -> {
            userController.createOrUpdateUsers(defaultUsers);
        };
    }

}
