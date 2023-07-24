package com.group2.server.controller;

import com.group2.server.dto.EntityReferenceDto;
import com.group2.server.dto.InstructorAvailabilityDto;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InstructorAvailabilityController {

    @Autowired
    private InstructorAvailabilityRepository instructorAvailabilityRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/instructor-availabilities")
    public ResponseEntity<List<InstructorAvailabilityDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(instructorAvailabilityRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/instructor-availabilities/{id}")
    public ResponseEntity<InstructorAvailabilityDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(instructorAvailabilityRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instructor-availabilities")
    public ResponseEntity<List<InstructorAvailabilityDto>> createOrUpdateList(
            @RequestBody List<InstructorAvailabilityDto> instructorAvailabilityDtoList) {
        try {
            return new ResponseEntity<>(
                    instructorAvailabilityDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/instructor-availabilities/{id}")
    public ResponseEntity<InstructorAvailabilityDto> updateOneById(@PathVariable Integer id,
            @RequestBody InstructorAvailabilityDto instructorAvailabilityDto) {
        try {
            if ((instructorAvailabilityDto.getId() != null) && !id.equals(instructorAvailabilityDto.getId())) {
                throw new IllegalArgumentException();
            }
            instructorAvailabilityDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(instructorAvailabilityDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/instructor-availabilities/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            instructorAvailabilityRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public InstructorAvailabilityDto toDto(InstructorAvailability instructorAvailability) {
        Instructor instructor = instructorRepository.findById(instructorAvailability.getInstructor().getId()).get();
        return new InstructorAvailabilityDto(instructorAvailability.getId(), instructorAvailability.getDayOfWeek(),
                instructorAvailability.getPartOfDay(), new EntityReferenceDto(instructor.getId()));
    }

    public InstructorAvailability createOrUpdateFromDto(InstructorAvailabilityDto instructorAvailabilityDto) {
        InstructorAvailability instructorAvailability;
        if (instructorAvailabilityDto.getId() != null) {
            instructorAvailability = instructorAvailabilityRepository.findById(instructorAvailabilityDto.getId()).get();
        } else {
            Instructor instructor = instructorRepository.findById(instructorAvailabilityDto.getInstructor().getId())
                    .get();
            instructorAvailability = new InstructorAvailability(null, instructorAvailabilityDto.getDayOfWeek(),
                    instructorAvailabilityDto.getPartOfDay(), instructor);
        }
        return instructorAvailability;
    }

}
