package com.group2.server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "https://ficschedulerapp.onrender.com", allowCredentials = "true")
public class CoordinatorController {
    
    @GetMapping("/coordinator")
    public String verifiedCoordinatorController() {
        return "COORDINATOR level access BABYY"; 
    }

}
