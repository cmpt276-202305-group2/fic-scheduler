package com.group2.server.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.group2.server.model.*;
import com.group2.server.repository.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClassScheduleController {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @GetMapping("/schedules/latest")
    public ClassSchedule readLatestSchedule() {
        ClassSchedule latestSchedule = null;
        int latestId = -1;

        for (var sched : classScheduleRepository.findAll()) {
            if ((int) sched.getId() > latestId) {
                latestSchedule = sched;
                latestId = (int) sched.getId();
            }
        }

        if (latestSchedule == null) {
            latestSchedule = new ClassSchedule();
            latestSchedule.setSemester("Fall 2023");
            HashSet<ClassScheduleAssignment> assignments = new HashSet<>();
            var alfred = new Instructor(null, "Alfred", null);
            var shaniqua = new Instructor(null, "Shaniqua", null);
            var chenoa = new Instructor(null, "Chenoa", null);
            var room2400 = new Classroom(null, "DIS1 2400", null);
            var room2550 = new Classroom(null, "DIS1 2550", null);
            assignments.add(
                    new ClassScheduleAssignment(null, latestSchedule, "CMPT 120", PartOfDay.MORNING, room2400, chenoa));
            assignments.add(new ClassScheduleAssignment(null, latestSchedule, "PHYS 125", PartOfDay.AFTERNOON, room2550,
                    alfred));
            assignments.add(new ClassScheduleAssignment(null, latestSchedule, "ENGL 105W", PartOfDay.EVENING, room2400,
                    shaniqua));
            latestSchedule.setClassScheduleAssignments(assignments);
        }

        return latestSchedule;
    }

    @GetMapping("/schedules/{id}")
    public ClassSchedule readScheduleById(@PathVariable Integer id) {
        if (id == null) {
            return null;
        }
        return classScheduleRepository.findById(id).orElse(null);
    }

    @GetMapping("/schedules")
    public ClassSchedule[] readSchedulesByQuery(@RequestParam(required = false) String semester) {
        Collection<ClassSchedule> schedules;

        if (semester != null) {
            schedules = classScheduleRepository.findBySemester(semester);
        } else {
            schedules = classScheduleRepository.findAll();
        }

        return schedules.toArray(new ClassSchedule[0]);
    }

}
