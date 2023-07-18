package com.group2.server.controller;

import com.group2.server.model.Accreditation;
import com.group2.server.repository.AccreditationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
public class AccreditationController {

    private static final Logger logger = LoggerFactory.getLogger(AccreditationController.class);

    @Autowired
    private AccreditationRepository accreditationRepository;

    @PostMapping("/accreditations")
    public ResponseEntity<?> createAccreditations(@RequestBody List<Accreditation> accreditations) {
        try {
            List<Accreditation> createdAccreditations = new ArrayList<>();
            List<Accreditation> conflictAccreditations = new ArrayList<>();
            for (Accreditation accreditation : accreditations) {
                // Check if the accreditation already exists
                Accreditation existingAccreditation = accreditationRepository.findByName(accreditation.getName());
                if (existingAccreditation != null) {
                    logger.info("Conflict: An accreditation with name {} already exists", accreditation.getName());
                    conflictAccreditations.add(existingAccreditation);
                } else {
                    // If it doesn't exist, save the new accreditation
                    Accreditation _accreditation = accreditationRepository.save(new Accreditation(accreditation.getName()));
                    logger.info("Created new accreditation with name {}", _accreditation.getName());
                    createdAccreditations.add(_accreditation);
                }
            }
            if (!conflictAccreditations.isEmpty()) {
                // If there were conflict accreditations, return them along with the created accreditations
                Map<String, List<Accreditation>> response = new HashMap<>();
                response.put("created", createdAccreditations);
                response.put("conflicts", conflictAccreditations);
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                return new ResponseEntity<>(createdAccreditations, HttpStatus.CREATED);
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating accreditations: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accreditations")
    public ResponseEntity<List<Accreditation>> getAllAccreditations() {
        try {
            List<Accreditation> accreditations = accreditationRepository.findAll();
            if (accreditations.isEmpty()) {
                logger.info("No accreditations found");
            } else {
                logger.info("Found {} accreditations", accreditations.size());
            }
            return new ResponseEntity<>(accreditations, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching accreditations: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accreditations/{id}")
    public ResponseEntity<Accreditation> getAccreditationById(@PathVariable("id") Integer id) {
        Optional<Accreditation> accreditationData = accreditationRepository.findById(id);
        return accreditationData.map(accreditation -> new ResponseEntity<>(accreditation, HttpStatus.OK))
                                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/accreditations/{id}")
    public ResponseEntity<Accreditation> updateAccreditation(@PathVariable("id") Integer id, @RequestBody Accreditation accreditation) {
        Optional<Accreditation> accreditationData = accreditationRepository.findById(id);
        if (accreditationData.isPresent()) {
            Accreditation _accreditation = accreditationData.get();
            _accreditation.setName(accreditation.getName());
            return new ResponseEntity<>(accreditationRepository.save(_accreditation), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/accreditations/{id}")
    public ResponseEntity<HttpStatus> deleteAccreditation(@PathVariable("id") Integer id) {
        try {
            accreditationRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
