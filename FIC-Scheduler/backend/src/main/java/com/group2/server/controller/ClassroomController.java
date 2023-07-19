package com.group2.server.controller;

import com.group2.server.model.Classroom;
import com.group2.server.model.Facilities;
import com.group2.server.repository.ClassroomRepository;
import com.group2.server.repository.FacilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://ficschedulerapp.onrender.com", allowCredentials = "true")
public class ClassroomController {

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @PostMapping("/classroom")
    public ResponseEntity<?> createClassrooms(@RequestBody List<ClassroomDto> classroomDtos) {
        List<Classroom> savedClassrooms = new ArrayList<>();
        List<String> conflictClassrooms = new ArrayList<>();
        try {
            for (ClassroomDto classroomDto : classroomDtos) {
                Classroom existingClassroom = classroomRepository.findByRoomNumber(classroomDto.getRoomNumber());

                if (existingClassroom != null) {
                    conflictClassrooms.add(classroomDto.getRoomNumber());
                    continue;
                }

                Classroom classroom = new Classroom();
                classroom.setRoomNumber(classroomDto.getRoomNumber());

                Set<Facilities> facilities = new HashSet<>();
                for (String name : classroomDto.getFacilitiesAvailableNames()) {
                    Facilities facility = facilityRepository.findByName(name);
                    if (facility == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found with name " + name);
                    }
                    facilities.add(facility);
                }
                classroom.setFacilitiesAvailable(facilities);
                savedClassrooms.add(classroomRepository.save(classroom));
            }
            
            if (!conflictClassrooms.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("created", savedClassrooms);
                response.put("conflicts", conflictClassrooms);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(savedClassrooms, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @GetMapping("/classroom")
    public ResponseEntity<List<Classroom>> getAllClassrooms() {
        try {
            List<Classroom> classrooms = classroomRepository.findAll();
            return new ResponseEntity<>(classrooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
