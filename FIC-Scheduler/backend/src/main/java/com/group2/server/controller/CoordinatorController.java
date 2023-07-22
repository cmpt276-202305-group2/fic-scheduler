package com.group2.server.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class CoordinatorController {

    @GetMapping("/coordinator")
    public String verifiedCoordinatorController() {
        return "COORDINATOR level access BABYY";
    }

}
