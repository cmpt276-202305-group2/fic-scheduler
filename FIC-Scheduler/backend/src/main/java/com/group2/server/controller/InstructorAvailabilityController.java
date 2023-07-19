package com.group2.server.controller;

import com.group2.server.model.*;
import com.group2.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://ficschedulerapp.onrender.com/", allowCredentials = "true")
public class InstructorAvailabilityController {

    private static final Logger logger = LoggerFactory.getLogger(InstructorAvailabilityController.class);

    @Autowired
    private InstructorAvailabilityRepository instructorAvailabilityRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @PostMapping("/instructorAvailabilities")
    public ResponseEntity<?> createInstructorAvailabilities(@RequestBody List<InstructorAvailabilityDto> instructorAvailabilityDtos) {
        List<InstructorAvailabilityDto> savedInstructorAvailabilities = new ArrayList<>();
        List<InstructorAvailabilityDto> conflictInstructorAvailabilities = new ArrayList<>();

        try {
            for (InstructorAvailabilityDto instructorAvailabilityDto : instructorAvailabilityDtos) {
                Instructor instructor = instructorRepository.findByName(instructorAvailabilityDto.getInstructorName());
                if (instructor == null) {
                    logger.error("Instructor not found with name {}", instructorAvailabilityDto.getInstructorName());
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instructor not found with name " + instructorAvailabilityDto.getInstructorName());
                }

                Set<InstructorAvailability> instructorAvailabilities = instructorAvailabilityRepository.findByInstructor_Name(instructorAvailabilityDto.getInstructorName());
                InstructorAvailability instructorAvailability;

                // Check if the InstructorAvailability already exists
                if (instructorAvailabilities.isEmpty()) {
                    instructorAvailability = new InstructorAvailability();
                    instructorAvailability.setInstructor(instructor);
                    instructorAvailability.setAvailabilities(new ArrayList<>()); // initialize the list
                    instructorAvailability = instructorAvailabilityRepository.save(instructorAvailability);
                } else {
                    instructorAvailability = instructorAvailabilities.iterator().next();
                }

                // Check if an availability with the same day and part of day already exists
                boolean conflict = false;
                for (Availability existingAvailability : instructorAvailability.getAvailabilities()) {
                    if (existingAvailability.getDayOfWeek().equals(instructorAvailabilityDto.getDayOfWeek())
                            && existingAvailability.getPartOfDay().equals(instructorAvailabilityDto.getPartOfDay())) {
                        logger.warn("Availability for {} on {} {} already exists.", instructorAvailabilityDto.getInstructorName(),
                                instructorAvailabilityDto.getDayOfWeek(), instructorAvailabilityDto.getPartOfDay());
                        conflict = true;
                        break;
                    }
                }
                
                if (conflict) {
                    conflictInstructorAvailabilities.add(instructorAvailabilityDto);
                    continue;
                }

                Availability availability = new Availability();
                availability.setDayOfWeek(instructorAvailabilityDto.getDayOfWeek());
                availability.setPartOfDay(instructorAvailabilityDto.getPartOfDay());
                availability.setInstructorAvailability(instructorAvailability);
                availabilityRepository.save(availability);
                savedInstructorAvailabilities.add(instructorAvailabilityDto);
            }

            if (!conflictInstructorAvailabilities.isEmpty()) {
                // If there were conflict instructor availabilities, return them along with the created ones
                Map<String, Object> response = new HashMap<>();
                response.put("created", savedInstructorAvailabilities);
                response.put("conflicts", conflictInstructorAvailabilities);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(savedInstructorAvailabilities, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating instructor availabilities: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/instructorAvailabilities")
    public ResponseEntity<List<InstructorAvailability>> getAllInstructorAvailabilities() {
        logger.info("Started fetching all instructor availabilities"); // New log entry
        try {
            List<InstructorAvailability> instructorAvailabilities = instructorAvailabilityRepository.findAll();
            if (instructorAvailabilities.isEmpty()) {
                logger.info("No instructor availabilities found");
            } else {
                logger.info("Found {} instructor availabilities", instructorAvailabilities.size());
            }
            logger.info("Finished fetching all instructor availabilities"); // New log entry
            return new ResponseEntity<>(instructorAvailabilities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching instructor availabilities: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
