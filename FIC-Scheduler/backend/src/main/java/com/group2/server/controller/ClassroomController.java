package com.group2.server.controller;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ClassroomController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @GetMapping("/classrooms")
    public ResponseEntity<List<ClassroomDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(classroomRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/classrooms/{id}")
    public ResponseEntity<ClassroomDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(classroomRepository.findById(id).get()), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/classrooms")
    public ResponseEntity<List<ClassroomDto>> createOrUpdateList(@RequestBody List<ClassroomDto> classroomDtoList) {
        try {
            return new ResponseEntity<>(
                    classroomDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/classrooms/{id}")
    public ResponseEntity<ClassroomDto> updateOneById(@PathVariable Integer id,
            @RequestBody ClassroomDto classroomDto) {
        try {
            if ((classroomDto.getId() != null) && !id.equals(classroomDto.getId())) {
                throw new IllegalArgumentException();
            }
            classroomDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(classroomDto)), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/classrooms/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            classroomRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ClassroomDto toDto(Classroom classroom) {
        return new ClassroomDto(classroom.getId(), classroom.getRoomNumber(), classroom.getRoomType());
    }

    public Classroom createOrUpdateFromDto(ClassroomDto classroomDto) {
        Classroom classroom;
        if (classroomDto.getId() != null) {
            classroom = classroomRepository.findById(classroomDto.getId()).get();
        } else {
            classroom = new Classroom(null, classroomDto.getRoomNumber(), classroomDto.getRoomType());
        }
        return classroom;
    }
}
