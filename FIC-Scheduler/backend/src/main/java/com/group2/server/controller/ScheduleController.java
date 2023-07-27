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

    @Autowired
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

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
            HashSet<ScheduleAssignment> assignments = new HashSet<>();
            var alfred = new Instructor(null, "Alfred", "");
            var shaniqua = new Instructor(null, "Shaniqua", "");
            var chenoa = new Instructor(null, "Chenoa", "");
            var twoHalfBlocks = blockRequirementSplitRepository.save(new BlockRequirementSplit(null,
                    "Two 1/2 Blocks", List.of(new BlockRequirement(null, Set.of(), Duration.HALF),
                            new BlockRequirement(null, Set.of(), Duration.HALF))));
            var oneFullBlock = blockRequirementSplitRepository.save(new BlockRequirementSplit(null,
                    "One Full Block", List.of(new BlockRequirement(null, Set.of(), Duration.FULL))));
            var oneFullPhysBlock = blockRequirementSplitRepository.save(new BlockRequirementSplit(null,
                    "One Full Block", List.of(new BlockRequirement(null, Set.of(), Duration.FULL))));
            var cmpt120 = new CourseOffering(null, "CMPT 120", "CMPT 120", "", Set.of(alfred, chenoa),
                    Set.of(twoHalfBlocks, oneFullBlock));
            var phys125 = new CourseOffering(null, "PHYS 125", "PHYS 125", "", Set.of(alfred, chenoa),
                    Set.of(oneFullPhysBlock));
            var engl105w = new CourseOffering(null, "ENGL 105W", "ENGL 105W", "", Set.of(alfred, chenoa),
                    Set.of(twoHalfBlocks, oneFullBlock));
            var room2400 = new Classroom(null, "DIS1 2400", "", "");
            var room2550 = new Classroom(null, "DIS1 2550", "", "");
            assignments
                    .add(new ScheduleAssignment(null, DayOfWeek.MONDAY, PartOfDay.MORNING, room2400, cmpt120, chenoa));
            assignments.add(
                    new ScheduleAssignment(null, DayOfWeek.MONDAY, PartOfDay.AFTERNOON, room2550, phys125, alfred));
            assignments.add(
                    new ScheduleAssignment(null, DayOfWeek.WEDNESDAY, PartOfDay.EVENING, room2400, engl105w, shaniqua));
            latestSchedule.setAssignments(assignments);
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
        for (var assignment : schedule.getAssignments()) {
            courses.computeIfAbsent(assignment.getCourse(),
                    (course) -> {
                        var dto = new CourseAssignmentDto();
                        dto.setCourse(new CourseOfferingDto(course.getId(), course.getName(), course.getCourseNumber(),
                                course.getNotes(),
                                course.getApprovedInstructors().stream()
                                        .map(i -> (EntityDto) new EntityReferenceDto(i.getId())).toList(),
                                course.getAllowedBlockSplits().stream()
                                        .map(bd -> (EntityDto) new EntityReferenceDto(bd.getId())).toList()));
                        return dto;
                    });
        }
        return new ScheduleDto(schedule.getId(), schedule.getName(), schedule.getNotes(), schedule.getSemester(),
                List.copyOf(courses.values()));
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
