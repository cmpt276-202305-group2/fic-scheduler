package com.group2.server.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/schedules/latest")
    public ResponseEntity<ScheduleDto> readLatestSchedule() {
        Schedule latestSchedule = null;
        int latestId = -1;

        for (var sched : scheduleRepository.findAll()) {
            if ((int) sched.getId() > latestId) {
                latestSchedule = sched;
                latestId = (int) sched.getId();
            }
        }

        // TODO remove test code
        if (latestSchedule == null) {
            latestSchedule = new Schedule();
            latestSchedule.setSemester("Fall 2023");
            HashSet<ClassScheduleAssignment> assignments = new HashSet<>();
            var alfred = new Instructor(null, "Alfred");
            var shaniqua = new Instructor(null, "Shaniqua");
            var chenoa = new Instructor(null, "Chenoa");
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

        return new ResponseEntity<>(toDto(latestSchedule), HttpStatus.OK);
    }

    @GetMapping("/schedules")
    public ResponseEntity<List<ScheduleDto>> readListByQuery(@RequestParam(required = false) String semester) {
        try {
            List<Schedule> schedules;
            if (semester != null) {
                schedules = scheduleRepository.findBySemester(semester).stream().toList();
            } else {
                schedules = scheduleRepository.findAll();
            }
            return new ResponseEntity<>(schedules.stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<ScheduleDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(scheduleRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/schedules")
    public ResponseEntity<List<ScheduleDto>> createOrUpdateList(
            @RequestBody List<ScheduleDto> scheduleDtoList) {
        try {
            return new ResponseEntity<>(
                    scheduleDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<ScheduleDto> updateOneById(@PathVariable Integer id,
            @RequestBody ScheduleDto scheduleDto) {
        try {
            if ((scheduleDto.getId() != null) && !id.equals(scheduleDto.getId())) {
                throw new IllegalArgumentException();
            }
            scheduleDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(scheduleDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            scheduleRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public ScheduleDto toDto(Schedule schedule) {
        return new ScheduleDto(schedule.getId(), schedule.getSemester());
    }

    public Schedule createOrUpdateFromDto(ScheduleDto scheduleDto) {
        Schedule schedule;
        if (scheduleDto.getId() != null) {
            schedule = scheduleRepository.findById(scheduleDto.getId()).get();
        } else {
            schedule = new Schedule(null, scheduleDto.getSemester(), null);
        }
        return schedule;
    }

}
