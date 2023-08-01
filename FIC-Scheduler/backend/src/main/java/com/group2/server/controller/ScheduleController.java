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

    @Autowired
    private CourseOfferingRepository courseOfferingRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private InstructorRepository instructorRepository;

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
        return new ResponseEntity<>(latestSchedule != null ? toDto(latestSchedule) : null, HttpStatus.OK);
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
            return new ResponseEntity<>(schedules.stream().map(this::toDto).toList(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/schedules/{id}")
    public ResponseEntity<ScheduleDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(scheduleRepository.findById(id).get()), HttpStatus.OK);
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
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(scheduleDto)), HttpStatus.OK);
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
        var courses = new HashMap<CourseOffering, CourseAssignmentDto>();
        if (schedule.getAssignments() != null) {
            for (var assignment : schedule.getAssignments()) {
                courses.compute(assignment.getCourse(),
                        (courseOffering, courseDto) -> {
                            if (courseDto == null) {
                                courseDto = new CourseAssignmentDto();
                                courseDto.setCourse(toDto(courseOffering));
                                courseDto.setBlocks(new ArrayList<>());
                            }
                            courseDto.getBlocks().add(new BlockAssignmentDto(toDto(assignment.getInstructor()),
                                    toDto(assignment.getClassroom()), assignment.getDayOfWeek(),
                                    assignment.getPartOfDay()));
                            return courseDto;
                        });
            }
        }
        return new ScheduleDto(schedule.getId(), schedule.getName(), schedule.getNotes(), schedule.getSemester(),
                List.copyOf(courses.values()));
    }

    public EntityDto toDto(CourseOffering courseOffering) {
        // return new CourseOfferingDto(courseOffering.getId(),
        // courseOffering.getName(),
        // courseOffering.getCourseNumber(),
        // courseOffering.getNotes(),
        // courseOffering.getApprovedInstructors().stream()
        // .map(i -> (EntityDto) new EntityReferenceDto(i.getId())).toList(),
        // courseOffering.getAllowedBlockSplits().stream()
        // .map(bd -> (EntityDto) new EntityReferenceDto(bd.getId())).toList());
        return new EntityReferenceDto(courseOffering.getId());
    }

    public EntityDto toDto(Instructor instructor) {
        return new EntityReferenceDto(instructor.getId());
    }

    public EntityDto toDto(Classroom classroom) {
        return new EntityReferenceDto(classroom.getId());
    }

    public Schedule createOrUpdateFromDto(ScheduleDto scheduleDto) {
        Schedule schedule;
        if (scheduleDto.getId() != null) {
            schedule = scheduleRepository.findById(scheduleDto.getId()).get();
            if (scheduleDto.getName() != null) {
                schedule.setName(scheduleDto.getName());
            }
            if (scheduleDto.getNotes() != null) {
                schedule.setNotes(scheduleDto.getNotes());
            }
            if (scheduleDto.getSemester() != null) {
                schedule.setSemester(scheduleDto.getSemester());
            }
            if (scheduleDto.getCourses() != null) {
                var assignments = assignmentsFromDto(scheduleDto);
                schedule.setAssignments(assignments);
            }
        } else {
            var assignments = assignmentsFromDto(scheduleDto);
            schedule = new Schedule(null, Optional.ofNullable(scheduleDto.getName()).orElse(""),
                    Optional.ofNullable(scheduleDto.getNotes()).orElse(""), scheduleDto.getSemester(), assignments);
        }
        return scheduleRepository.save(schedule);
    }

    private HashSet<ScheduleAssignment> assignmentsFromDto(ScheduleDto scheduleDto) {
        var assignments = new HashSet<ScheduleAssignment>();
        for (var course : scheduleDto.getCourses()) {
            var courseOffering = courseOfferingRepository.findById(course.getCourse().getId()).get();
            for (var block : course.getBlocks()) {
                var classroom = classroomRepository.findById(block.getClassroom().getId()).get();
                var instructor = instructorRepository.findById(block.getInstructor().getId()).get();
                assignments.add(new ScheduleAssignment(null, block.getDay(), block.getTime(), classroom,
                        courseOffering, instructor));
            }
        }
        return assignments;
    }

}
