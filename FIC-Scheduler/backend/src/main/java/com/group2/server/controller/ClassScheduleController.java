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

    // thingies thingy thingyDto thingyDtoList thingyRepository Thingy ThingyDto
    // @GetMapping("/thingies")
    // public ResponseEntity<List<ThingyDto>> readListByQuery() {
    // try {
    // return new
    // ResponseEntity<>(thingyRepository.findAll().stream().map(this::toDto).toList(),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @GetMapping("/thingies/{id}")
    // public ResponseEntity<ThingyDto> readOneById(@PathVariable Integer id) {
    // try {
    // return new ResponseEntity<>(toDto(thingyRepository.findById(id).get()),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PostMapping("/thingies")
    // public ResponseEntity<List<ThingyDto>> createOrUpdateList(@RequestBody
    // List<ThingyDto> thingyDtoList) {
    // try {
    // return new ResponseEntity<>(
    // thingyDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @PutMapping("/thingies/{id}")
    // public ResponseEntity<ThingyDto> updateOneById(@PathVariable Integer id,
    // @RequestBody ThingyDto userDto) {
    // try {
    // if ((thingyDto.getId() != null) && !id.equals(thingyDto.getId())) {
    // throw new IllegalArgumentException();
    // }
    // thingyDto.setId(id);
    // return new ResponseEntity<>(toDto(createOrUpdateFromDto(thingyDto)),
    // HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // @DeleteMapping("/thingies/{id}")
    // public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
    // try {
    // thingyRepository.deleteById(id);
    // return new ResponseEntity<>(HttpStatus.OK);
    // } catch (Exception e) {
    // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    // }
    // }

    // public ThingyDto toDto(Thingy thingy) {
    // return new ThingyDto(thingy.getId());
    // }

    // public Thingy createOrUpdateFromDto(ThingyDto thingyDto) {
    // Thingy thingy;
    // if (thingyDto.getId() != null) {
    // thingy = thingyRepository.findById(thingyDto.getId()).get();
    // } else {
    // thingy = new Thingy(null, ...);
    // }
    // return thingy;
    // }

}
