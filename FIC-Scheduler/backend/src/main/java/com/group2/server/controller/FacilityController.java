package com.group2.server.controller;

import com.group2.server.model.*;
import com.group2.server.repository.FacilityRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger; // tempppp!!
import org.slf4j.LoggerFactory; // tempppp!!

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class FacilityController {

    private static final Logger logger = LoggerFactory.getLogger(FacilityController.class); // tempppp!!

    @Autowired
    private FacilityRepository facilityRepository;

    @PostMapping("/facilities")
    public ResponseEntity<?> createFacilities(@RequestBody List<Facilities> facilities) {
        try {
            List<Facilities> createdFacilities = new ArrayList<>();
            List<Facilities> conflictFacilities = new ArrayList<>();
            for (Facilities facility : facilities) {
                // Check if the facility already exists
                Facilities existingFacility = facilityRepository.findByName(facility.getName());
                if (existingFacility != null) {
                    logger.info("Conflict: A facility with name {} already exists", facility.getName()); // Log conflict
                    conflictFacilities.add(existingFacility);
                } else {
                    // If it doesn't exist, save the new facility
                    Facilities _facility = facilityRepository.save(new Facilities(facility.getName()));
                    logger.info("Created new facility with name {}", _facility.getName()); // Log creation
                    createdFacilities.add(_facility);
                }
            }
            if (!conflictFacilities.isEmpty()) {
                // If there were conflict facilities, return them along with the created
                // facilities
                Map<String, List<Facilities>> response = new HashMap<>();
                response.put("created", createdFacilities);
                response.put("conflicts", conflictFacilities);
                List<Facilities> allFacilities = facilityRepository.findAll();
                logger.info("State of the facilities repository after POST: {}", allFacilities); // tempppp!!
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(createdFacilities, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating facilities: {}", e.getMessage()); // Log exception
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/facilities")
    public ResponseEntity<List<Facilities>> getAllFacilities() {
        try {
            List<Facilities> facilities = facilityRepository.findAll();
            if (facilities.isEmpty()) {
                logger.info("No facilities found"); // Log if no facilities found
            } else {
                logger.info("Found {} facilities", facilities.size()); // Log the number of facilities found
            }
            return new ResponseEntity<>(facilities, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching facilities: {}", e.getMessage()); // Log exception
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/facilities/{id}")
    public ResponseEntity<Facilities> getFacilityById(@PathVariable("id") Integer id) {
        Optional<Facilities> facilityData = facilityRepository.findById(id);

        return facilityData.map(facility -> new ResponseEntity<>(facility, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/facilities/{id}")
    public ResponseEntity<Facilities> updateFacility(@PathVariable("id") Integer id, @RequestBody Facilities facility) {
        Optional<Facilities> facilityData = facilityRepository.findById(id);

        if (facilityData.isPresent()) {
            Facilities _facility = facilityData.get();
            _facility.setName(facility.getName());
            return new ResponseEntity<>(facilityRepository.save(_facility), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/facilities/{id}")
    public ResponseEntity<HttpStatus> deleteFacility(@PathVariable("id") Integer id) {
        try {
            facilityRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
