package com.group2.server.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class InstructorController {

    @GetMapping("/instructor")
    public String verifiedInstructorController() {
        return "instructor level access";
    }

}
