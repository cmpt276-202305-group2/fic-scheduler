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
public class SemesterPlanController {

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @GetMapping("/semester-plans")
    public ResponseEntity<List<SemesterPlanDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(semesterPlanRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/semester-plans/{id}")
    public ResponseEntity<SemesterPlanDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(semesterPlanRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/semester-plans")
    public ResponseEntity<List<SemesterPlanDto>> createOrUpdateList(
            @RequestBody List<SemesterPlanDto> semesterPlanDtoList) {
        try {
            return new ResponseEntity<>(
                    semesterPlanDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/semester-plans/{id}")
    public ResponseEntity<SemesterPlanDto> updateOneById(@PathVariable Integer id,
            @RequestBody SemesterPlanDto semesterPlanDto) {
        try {
            if ((semesterPlanDto.getId() != null) && !id.equals(semesterPlanDto.getId())) {
                throw new IllegalArgumentException();
            }
            semesterPlanDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(semesterPlanDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/semester-plans/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            semesterPlanRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public SemesterPlanDto toDto(SemesterPlan semesterPlan) {
        return new SemesterPlanDto(semesterPlan.getId(), semesterPlan.getSemester(),
                semesterPlan.getCoursesOffered().stream().map(c -> (EntityDto) new EntityReferenceDto(c.getId()))
                        .toList(),
                semesterPlan.getInstructorsAvailable().stream()
                        .map(ia -> new InstructorAvailabilityDto(ia.getDayOfWeek(), ia.getPartOfDay(),
                                new EntityReferenceDto(ia.getInstructor().getId())))
                        .toList(),
                semesterPlan.getClassroomsAvailable().stream().map(c -> (EntityDto) new EntityReferenceDto(c.getId()))
                        .toList());
    }

    public SemesterPlan createOrUpdateFromDto(SemesterPlanDto semesterPlanDto) {
        SemesterPlan semesterPlan;
        if (semesterPlanDto.getId() != null) {
            semesterPlan = semesterPlanRepository.findById(semesterPlanDto.getId()).get();
        } else {
            semesterPlan = new SemesterPlan();
        }
        return semesterPlan;
    }
}
