package com.group2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import java.util.*;

@RestController
@RequestMapping("/debug")
@CrossOrigin("*")
public class DebugController {

    // @Autowired
    // private ScheduleRepository classScheduleRepository;

    // @Autowired
    // private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @PostMapping("/add-test-instructors")
    public ResponseEntity<Void> addTestInstructors() {
        try {
            var r = new Random();
            var firstNames = new String[] { "Olivia", "Sophia", "Amelia", "Emma", "Ava", "Charlotte", "Lily", "Hannah",
                    "Nora", "Isabella", "Noah", "Liam", "Jackson", "Oliver", "Leo", "Lucas", "Luca", "Jack", "James",
                    "Benjamin" };
            var lastNames = new String[] { "Smith", "Brown", "Tremblay", "Martin", "Roy", "Gagnon", "Lee", "Wilson",
                    "Johnson", "MacDonald", "Taylor", "Campbell", "Anderson", "Jones", "Leblanc", "Cote", "Williams",
                    "Miller", "Thompson", "Gauthier" };

            var usedNames = new HashSet<String>();
            usedNames.addAll(instructorRepository.findAll().stream().map(i -> i.getName()).toList());
            while (usedNames.size() < 30) {
                String name = firstNames[r.nextInt(firstNames.length)] + " "
                        + lastNames[r.nextInt(lastNames.length)];
                if (!usedNames.contains(name)) {
                    instructorRepository
                            .save(new Instructor(null, name, "Created by DebugController.addTestInstructors"));
                    usedNames.add(name);
                }
            }
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-test-blocks")
    public ResponseEntity<Void> addTestBlocks() {
        try {
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-test-classrooms")
    public ResponseEntity<Void> addTestClassrooms() {
        try {
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/add-test-semester-plan")
    public ResponseEntity<Void> addTestSemesterPlan() {
        try {
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
