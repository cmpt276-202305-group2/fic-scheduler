package com.group2.server;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import com.group2.server.model.*;
import com.group2.server.repository.*;

import jakarta.transaction.Transactional;

@SpringBootApplication()
public class ServerApplication {

        @Autowired
        private BlockRequirementDivisionRepository blockRequirementRepository;

        @Value("${app.user.coordinator.username}")
        private String coordinatorUsername;

        @Value("${app.user.coordinator.password}")
        private String coordinatorPassword;

        @Value("${app.user.debug.username}")
        private String debugUsername;

        @Value("${app.user.debug.password}")
        private String debugPassword;

        public static void main(String[] args) {
                SpringApplication.run(ServerApplication.class, args);
        }

        @Bean
        @Transactional
        CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
                return args -> {
                        // TODO please remove in production
                        // coordinator
                        userRepository.save(new ApplicationUser(null, coordinatorUsername,
                                        passwordEncoder.encode(coordinatorPassword),
                                        Set.of(Role.COORDINATOR), "Coordinator"));

                        // TODO back dooooooor please remove in production
                        userRepository.save(new ApplicationUser(null, debugUsername,
                                        passwordEncoder.encode(debugPassword),
                                        Set.of(Role.ADMIN, Role.COORDINATOR, Role.INSTRUCTOR, Role.DEBUG),
                                        "Debug User"));

                        // Define standard BlockTypes
                        // halfBlock
                        blockRequirementRepository.save(new BlockRequirementDivision(null, "1 Half",
                                        List.of(new BlockRequirement(null, Set.of(), Duration.HALF))));
                        // fullBlock
                        blockRequirementRepository.save(new BlockRequirementDivision(null, "1 Full",
                                        List.of(new BlockRequirement(null, Set.of(), Duration.FULL))));
                        // twoHalfBlocks
                        blockRequirementRepository.save(new BlockRequirementDivision(null, "2 Full",
                                        List.of(new BlockRequirement(null, Set.of(), Duration.HALF),
                                                        new BlockRequirement(null, Set.of(), Duration.HALF))));
                        // twoFullBlocks
                        blockRequirementRepository.save(new BlockRequirementDivision(null, "2 Half",
                                        List.of(new BlockRequirement(null, Set.of(), Duration.FULL),
                                                        new BlockRequirement(null, Set.of(), Duration.FULL))));
                        // halfAndFullBlock
                        blockRequirementRepository.save(new BlockRequirementDivision(null, "1 Half and 1 Full",
                                        List.of(new BlockRequirement(null, Set.of(), Duration.HALF),
                                                        new BlockRequirement(null, Set.of(), Duration.FULL))));
                };
        }
}
