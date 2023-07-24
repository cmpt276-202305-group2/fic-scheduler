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
public class InstructorsController {

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/instructors")
    public ResponseEntity<List<InstructorDto>> readInstructors() {
        try {
            return new ResponseEntity<>(instructorRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/instructors")
    public ResponseEntity<List<InstructorDto>> createOrUpdateInstructors(
            @RequestBody List<InstructorDto> instructorDtos) {
        try {
            return new ResponseEntity<>(
                    instructorDtos.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/instructors/{id}")
    public ResponseEntity<Instructor> readInstructor(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(instructorRepository.findById(id).get(), HttpStatus.OK);
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

    // thingies thingy thingyDto thingyDtoList thingyRepository Thingy ThingyDto
    // @GetMapping("/thingies")
    // public ResponseEntity<List<ThingyDto>> readListByQuery() {
    // try {
    // return new
    // ResponseEntity<>(thingyRepository.findAll().stream().map(this::toDto).toList(),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @GetMapping("/thingies/{id}")
    // public ResponseEntity<ThingyDto> readOneById(@PathVariable Integer id) {
    // try {
    // return new ResponseEntity<>(toDto(thingyRepository.findById(id).get()),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PostMapping("/thingies")
    // public ResponseEntity<List<ThingyDto>> createOrUpdateList(@RequestBody
    // List<ThingyDto> thingyDtoList) {
    // try {
    // return new ResponseEntity<>(
    // thingyDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PutMapping("/thingies/{id}")
    // public ResponseEntity<ThingyDto> updateOneById(@PathVariable Integer id,
    // @RequestBody ThingyDto userDto) {
    // try {
    // if ((thingyDto.getId() != null) && !id.equals(thingyDto.getId())) {
    // throw new IllegalArgumentException();
    // }
    // thingyDto.setId(id);
    // return new ResponseEntity<>(toDto(createOrUpdateFromDto(thingyDto)),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @DeleteMapping("/thingies/{id}")
    // public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
    // try {
    // thingyRepository.deleteById(id);
    // return new ResponseEntity<>(HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // public ThingyDto toDto(Thingy thingy) {
    // return new ThingyDto(thingy.getId());
    // }

    // public Thingy createOrUpdateFromDto(ThingyDto thingyDto) {
    // Thingy thingy;
    // if (thingyDto.getId() != null) {
    // thingy = classroomRepository.findById(thingyDto.getId()).get();
    // } else {
    // thingy = new Thingy(null, ...);
    // }
    // return thingy;
    // }

}
