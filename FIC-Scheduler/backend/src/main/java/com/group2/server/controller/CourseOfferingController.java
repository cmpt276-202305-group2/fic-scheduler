package com.group2.server.controller;

import com.group2.server.model.*;
import com.group2.server.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class CourseOfferingController {

    private static final Logger logger = LoggerFactory.getLogger(CourseOfferingController.class);

    @Autowired
    private CourseOfferingRepository courseOfferingRepository;

    @Autowired
    private AccreditationRepository accreditationRepository;

    // @Autowired
    // private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private BlockTypeRepository blockTypeRepository;

    @PostMapping("/courseOffering")
    public ResponseEntity<?> createCourseOfferings(@RequestBody List<CourseOfferingDto> courseOfferingDtos) {
        List<CourseOffering> savedCourseOfferings = new ArrayList<>();
        List<String> conflictCourseNumbers = new ArrayList<>();
        try {
            for (CourseOfferingDto courseOfferingDto : courseOfferingDtos) {
                CourseOffering existingCourseOffering = courseOfferingRepository
                        .findByCourseNumber(courseOfferingDto.getCourseNumber());

                // Check if the CourseOffering already exists
                if (existingCourseOffering != null) {
                    logger.info("Conflict: Course offering {} already exists", courseOfferingDto.getCourseNumber());
                    conflictCourseNumbers.add(courseOfferingDto.getCourseNumber());
                    continue;
                }

                CourseOffering courseOffering = new CourseOffering();
                courseOffering.setCourseNumber(courseOfferingDto.getCourseNumber());

                /*
                 * SemesterPlan semesterPlan =
                 * semesterPlanRepository.findById(courseOfferingDto.getSemesterPlanId())
                 * .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                 * "Semester plan not found with ID " + courseOfferingDto.getSemesterPlanId()));
                 * courseOffering.setSemesterPlan(semesterPlan);
                 */

                Accreditation accreditation = accreditationRepository
                        .findByName(courseOfferingDto.getAccreditationRequiredName());
                if (accreditation == null) {
                    logger.error("Accreditation not found with name {}",
                            courseOfferingDto.getAccreditationRequiredName());
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Accreditation not found with name " + courseOfferingDto.getAccreditationRequiredName());
                }
                courseOffering.setAccreditationRequired(accreditation);

                Set<Facilities> facilities = new HashSet<>();
                for (String name : courseOfferingDto.getFacilitiesRequiredNames()) {
                    Facilities facility = facilityRepository.findByName(name);
                    if (facility == null) {
                        logger.error("Facility not found with name {}", name);
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facility not found with name " + name);
                    }
                    facilities.add(facility);
                }
                courseOffering.setFacilitiesRequired(facilities);

                BlockType blockType = blockTypeRepository.findByName(courseOfferingDto.getBlockTypeName());
                if (blockType == null) {
                    logger.error("BlockType not found with name {}", courseOfferingDto.getBlockTypeName());
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "BlockType not found with name " + courseOfferingDto.getBlockTypeName());
                }
                courseOffering.setBlockType(blockType);

                courseOffering.setConflictCourseNumbers(courseOfferingDto.getConflictCourseNumbers());

                savedCourseOfferings.add(courseOfferingRepository.save(courseOffering));
                logger.info("Successfully created course offering {}", courseOffering.getCourseNumber());
            }

            if (!conflictCourseNumbers.isEmpty()) {
                // If there were conflict course numbers, return them along with the created
                // course offerings
                return new ResponseEntity<>(savedCourseOfferings, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(savedCourseOfferings, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating course offerings: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/courseOffering")
    public ResponseEntity<List<CourseOffering>> getAllCourseOfferings() {
        try {
            List<CourseOffering> courseOfferings = courseOfferingRepository.findAll();
            if (courseOfferings.isEmpty()) {
                logger.info("No course offerings found");
            } else {
                logger.info("Found {} course offerings", courseOfferings.size());
            }
            return new ResponseEntity<>(courseOfferings, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching course offerings: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
