package com.group2.server.controller;

import com.group2.server.model.Instructor;
import com.group2.server.model.Accreditation;
import com.group2.server.repository.InstructorRepository;
import com.group2.server.repository.AccreditationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InstructorsController {

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private AccreditationRepository accreditationRepository;

    @PostMapping("/instructors")
    public ResponseEntity<?> createInstructors(@RequestBody List<InstructorDto> instructorDtos) {
        List<Instructor> savedInstructors = new ArrayList<>();
        List<String> conflictInstructors = new ArrayList<>();

        try {
            for (InstructorDto instructorDto : instructorDtos) {
                Instructor existingInstructor = instructorRepository.findByName(instructorDto.getName());

                // Check if the Instructor already exists
                if (existingInstructor != null) {
                    conflictInstructors.add(instructorDto.getName());
                    continue;
                }

                Instructor instructor = new Instructor();
                instructor.setName(instructorDto.getName());

                Set<Accreditation> accreditations = new HashSet<>();
                for (String name : instructorDto.getAccreditationNames()) {
                    Accreditation accreditation = accreditationRepository.findByName(name);
                    if (accreditation == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "Accreditation not found with name " + name);
                    }
                    accreditations.add(accreditation);
                }
                instructor.setAccreditations(accreditations);
                savedInstructors.add(instructorRepository.save(instructor));
            }

            if (!conflictInstructors.isEmpty()) {
                // If there were conflict instructors, return them along with the created
                // instructors
                Map<String, Object> response = new HashMap<>();
                response.put("created", savedInstructors);
                response.put("conflicts", conflictInstructors);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(savedInstructors, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/instructors")
    public ResponseEntity<List<Instructor>> getAllInstructors() {
        try {
            List<Instructor> instructors = instructorRepository.findAll();
            return new ResponseEntity<>(instructors, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
