package com.group2.server.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group2.server.model.ClassSchedule;
import com.group2.server.repository.ClassScheduleRepository;

@RestController
@RequestMapping("/schedule")
@CrossOrigin("*")
public class ScheduleController {

    @Autowired
    private ClassScheduleRepository classScheduleRepository;

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
        sched.setSemester(body.getSemester());
        sched = classScheduleRepository.save(sched);
        return sched;
    }

}
