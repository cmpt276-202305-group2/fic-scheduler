package com.group2.server.controller;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group2.server.model.ClassSchedule;
import com.group2.server.model.ClassScheduleAssignment;
import com.group2.server.model.Classroom;
import com.group2.server.model.Instructor;
import com.group2.server.model.InstructorAvailability;
import com.group2.server.model.PartOfDay;
import com.group2.server.model.SemesterPlan;
import com.group2.server.repository.ClassScheduleRepository;
import com.group2.server.repository.SemesterPlanRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ScheduleController {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @GetMapping("/schedules/latest")
    public ClassSchedule getLatestSchedule() {
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
            assignments.add(new ClassScheduleAssignment(null, latestSchedule, "CMPT 120", PartOfDay.MORNING, room2400, chenoa));
            assignments.add(new ClassScheduleAssignment(null, latestSchedule, "PHYS 125", PartOfDay.AFTERNOON, room2550, alfred));
            assignments.add(new ClassScheduleAssignment(null, latestSchedule, "ENGL 105W", PartOfDay.EVENING, room2400, shaniqua));
            latestSchedule.setClassScheduleAssignments(assignments);
        }

        return latestSchedule;
    }

    @GetMapping("/schedules/{id}")
    public ClassSchedule getScheduleById(@PathVariable Integer id) {
        if (id == null) {
            return null;
        }
        return classScheduleRepository.findById(id).orElse(null);
    }

    @GetMapping("/schedules")
    public ClassSchedule[] getSchedulesByQuery(@RequestParam(required = false) String semester) {
        Collection<ClassSchedule> schedules;

        if (semester != null) {
            schedules = classScheduleRepository.findBySemester(semester);
        } else {
            schedules = classScheduleRepository.findAll();
        }

        return schedules.toArray(new ClassSchedule[0]);
    }

    @PostMapping("/generate")
    public ClassSchedule generateSchedule(@RequestBody GenerateScheduleDto body) {
        ClassSchedule sched = new ClassSchedule();
        Integer planId = body.getSemesterPlanId();
        SemesterPlan plan = planId != null ? semesterPlanRepository.findById(planId).orElse(null) : null;

        if (plan == null) {
            return null;
        }

        sched.setSemester(plan.getSemester());

        // TODO test code just generates a random schedule -- make it real
        HashSet<ClassScheduleAssignment> assignments = new HashSet<>();
        var r = new Random();
        var partsOfDay = new PartOfDay[] { PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING };
        var classrooms = plan.getClassroomsAvailable().toArray(new Classroom[0]);
        var instructor_availabilities = plan.getInstructorsAvailable().toArray(new InstructorAvailability[0]);

        for (var offering : plan.getCoursesOffered()) {
            var partOfDay = partsOfDay[r.nextInt(partsOfDay.length)];
            var classroom = classrooms[r.nextInt(classrooms.length)];
            var instructor = instructor_availabilities[r.nextInt(instructor_availabilities.length)].getInstructor();
            assignments.add(new ClassScheduleAssignment(null, sched, offering.getCourseNumber(), partOfDay, classroom,
                    instructor));
        }
        sched.setClassScheduleAssignments(assignments);
        return sched;
    }

}
