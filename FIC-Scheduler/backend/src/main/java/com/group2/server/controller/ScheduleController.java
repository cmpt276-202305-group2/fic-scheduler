package com.group2.server.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group2.server.model.ClassSchedule;
import com.group2.server.model.ClassScheduleAssignment;
import com.group2.server.model.Classroom;
import com.group2.server.model.InstructorAvailability;
import com.group2.server.model.PartOfDay;
import com.group2.server.model.SemesterPlan;
import com.group2.server.repository.ClassScheduleRepository;
import com.group2.server.repository.SemesterPlanRepository;

@RestController
@RequestMapping("/schedule")
@CrossOrigin("*")
public class ScheduleController {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @GetMapping("/id/{id}")
    public ClassSchedule getScheduleById(@PathVariable Integer id) {
        if (id == null) {
            return null;
        }
        return classScheduleRepository.findById(id).orElse(null);
    }

    @GetMapping("/sememster/{semester}")
    public Integer[] findSchedulesBySemester(@PathVariable String semester) {
        ArrayList<Integer> ids = new ArrayList<>();

        for (var sched : classScheduleRepository.findBySemester(semester)) {
            ids.add(sched.getId());
        }

        return (Integer[]) ids.toArray();
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
