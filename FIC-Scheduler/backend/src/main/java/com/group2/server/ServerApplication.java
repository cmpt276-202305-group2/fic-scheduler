package com.group2.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

import com.group2.server.model.*;
import com.group2.server.repository.*;

@SpringBootApplication()
public class ServerApplication {

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private BlockRequirementRepository blockRequirementRepository;

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
    CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // TODO please remove in production
            Set<Role> coordinatorRoles = new HashSet<Role>();
            coordinatorRoles.add(Role.COORDINATOR);
            ApplicationUser coordinator = new ApplicationUser(null, coordinatorUsername,
                    passwordEncoder.encode(coordinatorPassword),
                    coordinatorRoles, "Coordinator");

            userRepository.save(coordinator);

            // TODO back dooooooor please remove in production
            Set<Role> debugRoles = new HashSet<Role>();
            debugRoles.add(Role.ADMIN);
            debugRoles.add(Role.COORDINATOR);
            debugRoles.add(Role.INSTRUCTOR);
            debugRoles.add(Role.DEBUG);
            ApplicationUser debug = new ApplicationUser(null, debugUsername, passwordEncoder.encode(debugPassword),
                    debugRoles, "Debug User");

            userRepository.save(debug);

            // Define Durations for BlockTypes
            List<Block> halfBlockDurations = new ArrayList<>();
            halfBlockDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.HALF)));

            List<Block> fullBlockDurations = new ArrayList<>();
            fullBlockDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.FULL)));

            List<Block> twoHalfBlocksDurations = new ArrayList<>();
            twoHalfBlocksDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.HALF)));
            twoHalfBlocksDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.HALF)));

            List<Block> twoFullBlocksDurations = new ArrayList<>();
            twoFullBlocksDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.FULL)));
            twoFullBlocksDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.FULL)));

            List<Block> halfAndFullBlockDurations = new ArrayList<>();
            halfAndFullBlockDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.HALF)));
            halfAndFullBlockDurations.add(blockRepository.save(new Block(null, new HashSet<>(), Duration.FULL)));

            // Define BlockTypes
            BlockRequirement halfBlock = new BlockRequirement(null, "1 Half", halfBlockDurations);
            BlockRequirement fullBlock = new BlockRequirement(null, "1 Full", fullBlockDurations);
            BlockRequirement twoHalfBlocks = new BlockRequirement(null, "2 Full", twoHalfBlocksDurations);
            BlockRequirement twoFullBlocks = new BlockRequirement(null, "2 Half", twoFullBlocksDurations);
            BlockRequirement halfAndFullBlock = new BlockRequirement(null, "1 Half and 1 Full",
                    halfAndFullBlockDurations);

            // Save BlockTypes
            blockRequirementRepository.save(halfBlock);
            blockRequirementRepository.save(fullBlock);
            blockRequirementRepository.save(twoHalfBlocks);
            blockRequirementRepository.save(twoFullBlocks);
            blockRequirementRepository.save(halfAndFullBlock);
        };
    }
}
