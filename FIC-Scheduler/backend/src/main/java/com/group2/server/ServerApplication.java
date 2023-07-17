package com.group2.server;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Role;
import com.group2.server.repository.UserRepository;

@SpringBootApplication()
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			Set<Role> roles = new HashSet<>();
			roles.add(Role.COORDINATOR);

			ApplicationUser coordinator = new ApplicationUser(null, "coordinator", passwordEncoder.encode("password"),
					roles);

			userRepository.save(coordinator);
		};

	}
}
