package com.group2.server;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.group2.server.Model.ApplicationUser;
import com.group2.server.Model.Role;
import com.group2.server.Repository.RoleRepository;
import com.group2.server.Repository.UserRepository;


@SpringBootApplication()
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}


	@Bean
	CommandLineRunner run(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder){
		return args -> {
			if(roleRepository.findByAuthority("COORDINATOR").isPresent()) return;
			Role coordinatorRole = roleRepository.save(new Role("COORDINATOR"));
			roleRepository.save(new Role("INSTRUCTOR"));

			Set<Role> roles = new HashSet<>();
			roles.add(coordinatorRole);

			ApplicationUser coordinator = new ApplicationUser(1, "coordinator", passwordEncoder.encode("password"), roles);

			userRepository.save(coordinator);
		};

	}
}
