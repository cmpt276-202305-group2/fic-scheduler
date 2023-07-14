package com.group2.server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedule")
@CrossOrigin("*")
public class ScheduleController {

    @GetMapping("/instructor")
    public String verifiedInstructorController() {
        return "instructor level access";
    }

    @PostMapping("/generate")
    public String generateSchedule(@RequestBody GenerateScheduleDto body) {
        return "admin level access";
    }

}
