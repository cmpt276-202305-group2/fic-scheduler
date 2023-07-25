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
public class CourseOfferingController {

    @Autowired
    private CourseOfferingRepository courseOfferingRepository;

    @Autowired
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @GetMapping("/course-offerings")
    public ResponseEntity<List<CourseOfferingDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(courseOfferingRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/course-offerings/{id}")
    public ResponseEntity<CourseOfferingDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(courseOfferingRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/course-offerings")
    public ResponseEntity<List<CourseOfferingDto>> createOrUpdateList(
            @RequestBody List<CourseOfferingDto> courseOfferingDtoList) {
        try {
            return new ResponseEntity<>(
                    courseOfferingDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/course-offerings/{id}")
    public ResponseEntity<CourseOfferingDto> updateOneById(@PathVariable Integer id,
            @RequestBody CourseOfferingDto courseOfferingDto) {
        try {
            if ((courseOfferingDto.getId() != null) && !id.equals(courseOfferingDto.getId())) {
                throw new IllegalArgumentException();
            }
            courseOfferingDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(courseOfferingDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/course-offerings/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            courseOfferingRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public CourseOfferingDto toDto(CourseOffering courseOffering) {
        return new CourseOfferingDto(courseOffering.getId(), courseOffering.getName(), courseOffering.getCourseNumber(),
                courseOffering.getNotes(),
                courseOffering.getApprovedInstructors().stream().map(i -> (EntityDto) new EntityReferenceDto(i.getId()))
                        .toList(),
                courseOffering.getAllowedBlockSplits().stream()
                        .map(bd -> (EntityDto) new EntityReferenceDto(bd.getId())).toList());
    }

    public CourseOffering createOrUpdateFromDto(CourseOfferingDto courseOfferingDto) {
        CourseOffering courseOffering;
        if (courseOfferingDto.getId() != null) {
            courseOffering = courseOfferingRepository.findById(courseOfferingDto.getId()).get();
            if (courseOfferingDto.getName() != null) {
                courseOffering.setName(courseOfferingDto.getName());
            }
            if (courseOfferingDto.getCourseNumber() != null) {
                courseOffering.setCourseNumber(courseOfferingDto.getCourseNumber());
            }
            if (courseOfferingDto.getNotes() != null) {
                courseOffering.setNotes(courseOfferingDto.getNotes());
            }
            if (courseOfferingDto.getApprovedInstructors() != null) {
                var approvedInstructors = Set.copyOf(courseOfferingDto.getApprovedInstructors().stream()
                        .map(i -> instructorRepository.findById(i.getId()).get()).toList());
                courseOffering.setApprovedInstructors(approvedInstructors);
            }
            if (courseOfferingDto.getAllowedBlockSplits() != null) {
                var allowedBlockSplits = Set.copyOf(courseOfferingDto.getAllowedBlockSplits().stream()
                        .map(bd -> blockRequirementSplitRepository.findById(bd.getId()).get()).toList());
                courseOffering.setAllowedBlockSplits(allowedBlockSplits);
            }
        } else {
            var approvedInstructorsDto = Optional.ofNullable(courseOfferingDto.getApprovedInstructors())
                    .orElse(List.of());
            var allowedBlockSplitsDto = Optional.ofNullable(courseOfferingDto.getAllowedBlockSplits())
                    .orElse(List.of());
            var approvedInstructors = Set.copyOf(
                    approvedInstructorsDto.stream().map(i -> instructorRepository.findById(i.getId()).get()).toList());
            var allowedBlockSplits = Set.copyOf(allowedBlockSplitsDto.stream()
                    .map(bd -> blockRequirementSplitRepository.findById(bd.getId()).get()).toList());
            courseOffering = new CourseOffering(null, Optional.ofNullable(courseOfferingDto.getName()).orElse(""),
                    Optional.ofNullable(courseOfferingDto.getCourseNumber()).orElse(""),
                    Optional.ofNullable(courseOfferingDto.getNotes()).orElse(""), approvedInstructors,
                    allowedBlockSplits);
        }
        return courseOfferingRepository.save(courseOffering);
    }

}
