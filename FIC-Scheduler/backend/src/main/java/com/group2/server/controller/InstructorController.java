package com.group2.server.controller;

import com.group2.server.dto.InstructorDto;
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
public class InstructorController {

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/instructors")
    public ResponseEntity<List<InstructorDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(instructorRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/instructors/{id}")
    public ResponseEntity<InstructorDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(instructorRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instructors")
    public ResponseEntity<List<InstructorDto>> createOrUpdateList(@RequestBody List<InstructorDto> instructorDtoList) {
        try {
            return new ResponseEntity<>(
                    instructorDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/instructors/{id}")
    public ResponseEntity<InstructorDto> updateOneById(@PathVariable Integer id,
            @RequestBody InstructorDto instructorDto) {
        try {
            if ((instructorDto.getId() != null) && !id.equals(instructorDto.getId())) {
                throw new IllegalArgumentException();
            }
            instructorDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(instructorDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/instructors/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            instructorRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public InstructorDto toDto(Instructor instructor) {
        return new InstructorDto(instructor.getId(), instructor.getName());
    }

    public Instructor createOrUpdateFromDto(InstructorDto instructorDto) {
        Instructor instructor;
        if (instructorDto.getId() != null) {
            instructor = instructorRepository.findById(instructorDto.getId()).get();
        } else {
            instructor = new Instructor(null, instructorDto.getName());
        }
        return instructor;
    }

}
