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

import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Availability;
import com.group2.server.model.BlockType;
import com.group2.server.model.DayOfWeek;
import com.group2.server.model.Duration;
import com.group2.server.model.PartOfDay;
import com.group2.server.model.Role;
import com.group2.server.repository.AvailabilityRepository;
import com.group2.server.repository.BlockTypeRepository;
import com.group2.server.repository.UserRepository;


@SpringBootApplication()
public class ServerApplication {

    @Autowired
    private BlockTypeRepository blockTypeRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

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
            ApplicationUser coordinator = new ApplicationUser(null, coordinatorUsername, passwordEncoder.encode(coordinatorPassword),
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
            List<Duration> halfBlockDurations = new ArrayList<>();
            halfBlockDurations.add(Duration.HALF);

            List<Duration> fullBlockDurations = new ArrayList<>();
            fullBlockDurations.add(Duration.FULL);

            List<Duration> twoHalfBlocksDurations = new ArrayList<>();
            twoHalfBlocksDurations.add(Duration.HALF);
            twoHalfBlocksDurations.add(Duration.HALF);

            List<Duration> twoFullBlocksDurations = new ArrayList<>();
            twoFullBlocksDurations.add(Duration.FULL);
            twoFullBlocksDurations.add(Duration.FULL);

            List<Duration> halfAndFullBlockDurations = new ArrayList<>();
            halfAndFullBlockDurations.add(Duration.HALF);
            halfAndFullBlockDurations.add(Duration.FULL);

            // Define BlockTypes
            BlockType halfBlock = new BlockType(null, "1 Half", halfBlockDurations);
            BlockType fullBlock = new BlockType(null, "1 Full", fullBlockDurations);
            BlockType twoHalfBlocks = new BlockType(null, "2 Full", twoHalfBlocksDurations);
            BlockType twoFullBlocks = new BlockType(null, "2 Half", twoFullBlocksDurations);
            BlockType halfAndFullBlock = new BlockType(null, "1 Half and 1 Full", halfAndFullBlockDurations);

            // Save BlockTypes
            blockTypeRepository.save(halfBlock);
            blockTypeRepository.save(fullBlock);
            blockTypeRepository.save(twoHalfBlocks);
            blockTypeRepository.save(twoFullBlocks);
            blockTypeRepository.save(halfAndFullBlock);

            // Create availability for every day and part of day
            for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
                for (PartOfDay partOfDay : PartOfDay.values()) {
                    Availability availability = new Availability();
                    availability.setDayOfWeek(dayOfWeek);
                    availability.setPartOfDay(partOfDay);
                    availabilityRepository.save(availability);
                }
            }
        };
    }
}
