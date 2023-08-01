package com.group2.server.controller;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class SemesterPlanController {

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private CourseOfferingRepository courseOfferingRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private InstructorAvailabilityRepository instructorAvailabilityRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @GetMapping("/semester-plans")
    public ResponseEntity<List<SemesterPlanDto>> readListByQuery() {
        try {
            return new ResponseEntity<>(semesterPlanRepository.findAll().stream().map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/semester-plans/{id}")
    public ResponseEntity<SemesterPlanDto> readOneById(@PathVariable Integer id) {
        try {
            return new ResponseEntity<>(toDto(semesterPlanRepository.findById(id).get()),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/semester-plans/latest")
    public ResponseEntity<?> readLatestSemesterPlan() {
        SemesterPlan latestSemesterPlan = null;
        int latestId = -1;

        for (var semesterPlan : semesterPlanRepository.findAll()) {
            if ((int) semesterPlan.getId() > latestId) {
                latestSemesterPlan = semesterPlan;
                latestId = (int) semesterPlan.getId();
            }
        }

        if (latestSemesterPlan == null) {
            // Respond with an error message if no semester plans exist
            Map<String, String> error = new HashMap<>();
            error.put("error", "No semester plans in the database");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(toDto(latestSemesterPlan), HttpStatus.OK);
    }

    @PostMapping("/semester-plans")
    public ResponseEntity<List<SemesterPlanDto>> createOrUpdateList(
            @RequestBody List<SemesterPlanDto> semesterPlanDtoList) {
        try {
            return new ResponseEntity<>(
                    semesterPlanDtoList.stream().map(this::createOrUpdateFromDto).map(this::toDto).toList(),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/semester-plans/{id}")
    public ResponseEntity<SemesterPlanDto> updateOneById(@PathVariable Integer id,
            @RequestBody SemesterPlanDto semesterPlanDto) {
        try {
            if ((semesterPlanDto.getId() != null) && !id.equals(semesterPlanDto.getId())) {
                throw new IllegalArgumentException();
            }
            semesterPlanDto.setId(id);
            return new ResponseEntity<>(toDto(createOrUpdateFromDto(semesterPlanDto)),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/semester-plans/{id}")
    public ResponseEntity<Void> deleteOneById(@PathVariable Integer id) {
        try {
            semesterPlanRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public SemesterPlanDto toDto(SemesterPlan semesterPlan) {
        return new SemesterPlanDto(semesterPlan.getId(), semesterPlan.getName(), semesterPlan.getNotes(),
                semesterPlan.getSemester(),
                semesterPlan.getCoursesOffered().stream().map(this::toDto).toList(),
                semesterPlan.getInstructorsAvailable().stream().map(this::toDto).toList(),
                semesterPlan.getClassroomsAvailable().stream().map(this::toDto).toList(),
                semesterPlan.getCourseCorequisites().stream().map(this::toDto).toList(),
                semesterPlan.getInstructorSchedulingRequests().stream().map(this::toDto).toList());
    }

    public EntityDto toDto(CourseOffering co) {
        return new EntityReferenceDto(co.getId());
    }

    public InstructorAvailabilityDto toDto(InstructorAvailability ia) {
        return new InstructorAvailabilityDto(ia.getDayOfWeek(),
                ia.getPartOfDay(),
                new EntityReferenceDto(ia.getInstructor().getId()));
    }

    public EntityDto toDto(Classroom c) {
        return new EntityReferenceDto(c.getId());
    }

    public CourseCorequisiteDto toDto(CourseCorequisite cc) {
        return new CourseCorequisiteDto(
                new EntityReferenceDto(cc.getCourseA().getId()),
                new EntityReferenceDto(cc.getCourseB().getId()));
    }

    public InstructorSchedulingRequestDto toDto(InstructorSchedulingRequest isr) {
        return new InstructorSchedulingRequestDto(
                new EntityReferenceDto(isr.getInstructor().getId()),
                isr.getSchedulingRequest());
    }

    public SemesterPlan createOrUpdateFromDto(SemesterPlanDto semesterPlanDto) {
        SemesterPlan semesterPlan;

        if (semesterPlanDto.getId() != null) {
            semesterPlan = semesterPlanRepository.findById(semesterPlanDto.getId()).get();
            if (semesterPlanDto.getName() != null) {
                semesterPlan.setName(semesterPlanDto.getName());
            }
            if (semesterPlanDto.getSemester() != null) {
                semesterPlan.setSemester(semesterPlanDto.getSemester());
            }
            if (semesterPlanDto.getNotes() != null) {
                semesterPlan.setNotes(semesterPlanDto.getNotes());
            }
            if (semesterPlanDto.getCoursesOffered() != null) {
                updateSetFromDto(
                        semesterPlanDto.getCoursesOffered(),
                        EntityDto::getId,
                        semesterPlan.getCoursesOffered(),
                        Entity::getId,
                        (id) -> courseOfferingRepository.findById(id).get());
            }
            if (semesterPlanDto.getInstructorsAvailable() != null) {
                updateSetFromDto(
                        semesterPlanDto.getInstructorsAvailable(),
                        Function.identity(),
                        semesterPlan.getInstructorsAvailable(),
                        this::toDto,
                        this::createOrUpdateFromDto);
            }
            if (semesterPlanDto.getClassroomsAvailable() != null) {
                updateSetFromDto(
                        semesterPlanDto.getClassroomsAvailable(),
                        EntityDto::getId,
                        semesterPlan.getClassroomsAvailable(),
                        Entity::getId,
                        (id) -> classroomRepository.findById(id).get());
            }
        } else {
            Set<CourseOffering> coursesOffered = null;
            if (semesterPlanDto.getCoursesOffered() != null) {
                coursesOffered = Set.copyOf(semesterPlanDto.getCoursesOffered().stream()
                        .map(c -> courseOfferingRepository.findById(c.getId()).get()).toList());
            }

            Set<InstructorAvailability> instructorsAvailable = Set
                    .copyOf(semesterPlanDto.getInstructorsAvailable().stream()
                            .map(this::createOrUpdateFromDto)
                            .toList());

            Set<Classroom> classroomsAvailable = null;
            if (semesterPlanDto.getClassroomsAvailable() != null) {
                classroomsAvailable = Set.copyOf(semesterPlanDto.getClassroomsAvailable().stream()
                        .map(c -> classroomRepository.findById(c.getId()).get()).toList());
            }

            Set<CourseCorequisite> courseCorequisites = null;
            if (semesterPlanDto.getCourseCorequisites() != null) {
                courseCorequisites = Set.copyOf(semesterPlanDto.getCourseCorequisites().stream()
                        .map(cc -> new CourseCorequisite(null,
                                courseOfferingRepository.findById(cc.getCourseA().getId()).get(),
                                courseOfferingRepository.findById(cc.getCourseB().getId()).get()))
                        .toList());
            }

            Set<InstructorSchedulingRequest> instructorSchedulingRequests = null;
            if (semesterPlanDto.getInstructorSchedulingRequests() != null) {
                instructorSchedulingRequests = Set.copyOf(semesterPlanDto.getInstructorSchedulingRequests().stream()
                        .map(isr -> new InstructorSchedulingRequest(null,
                                instructorRepository.findById(isr.getInstructor().getId()).get(),
                                isr.getRequest()))
                        .toList());
            }

            semesterPlan = new SemesterPlan(null,
                    Optional.ofNullable(semesterPlanDto.getName()).orElse(""),
                    Optional.ofNullable(semesterPlanDto.getNotes()).orElse(""),
                    semesterPlanDto.getSemester(), coursesOffered, instructorsAvailable, classroomsAvailable,
                    courseCorequisites, instructorSchedulingRequests);
        }
        return semesterPlanRepository.save(semesterPlan);
    }

    public InstructorAvailability createOrUpdateFromDto(InstructorAvailabilityDto iaDto) {
        var instructor = instructorRepository.findById(iaDto.getInstructor().getId()).get();
        var ia = new InstructorAvailability(null, iaDto.getDayOfWeek(), iaDto.getPartOfDay(), instructor);
        return instructorAvailabilityRepository.save(ia);
    }

    private <T, U, V> void updateSetFromDto(
            List<T> dtoContainer,
            Function<T, V> getIdFromDto,
            Set<U> modelContainer,
            Function<U, V> getIdFromModel,
            Function<V, U> findModelById) {
        var dtoIds = Set.copyOf(dtoContainer.stream().map(getIdFromDto).toList());
        for (var model : List.copyOf(modelContainer)) {
            var id = getIdFromModel.apply(model);
            if (!dtoIds.contains(id)) {
                modelContainer.remove(model);
            }
        }
        var modelIds = Set.copyOf(modelContainer.stream().map(getIdFromModel).toList());
        for (var id : dtoIds) {
            if (!modelIds.contains(id)) {
                modelContainer.add(findModelById.apply(id));
            }
        }
    }
}
