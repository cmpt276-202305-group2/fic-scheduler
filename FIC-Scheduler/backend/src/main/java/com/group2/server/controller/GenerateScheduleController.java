package com.group2.server.controller;

import java.util.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;

import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import lombok.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GenerateScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleController scheduleController;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    @Autowired
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    private final Pattern baseNamePattern = Pattern.compile("^(.*?)(?>\\s*-\\s*[0-9]+)?$");

    @PostMapping("/generate-schedule")
    public ResponseEntity<ScheduleDto> generateSchedule(@RequestBody GenerateScheduleDto generateScheduleDto) {
        try {
            Integer planId = generateScheduleDto.getSemesterPlan().getId();
            SemesterPlan plan = planId != null ? semesterPlanRepository.findById(planId).orElse(null) : null;

            if (plan == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            var generator = new ScheduleGenerator(plan);
            List<Schedule> schedules = generator.generate();
            Schedule schedule;
            if (schedules.size() > 0) {
                schedule = schedules.get(0);
            } else {
                schedule = new Schedule();
                schedule.setNotes("Unable to generate a schedule! Check for irreconcilable conflicts.");
            }

            schedule = setupScheduleMetadata(plan, schedule);
            schedule = scheduleRepository.save(schedule);
            var scheduleDto = scheduleController.toDto(schedule);

            return new ResponseEntity<>(scheduleDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Schedule setupScheduleMetadata(SemesterPlan plan, Schedule schedule) {
        Set<String> scheduleNames = Set.copyOf(scheduleRepository.findAll().stream().map(s -> s.getName()).toList());
        String name = plan.getName();
        if (scheduleNames.contains(name)) {
            String baseName = name;
            Matcher nameMatcher = baseNamePattern.matcher(name);
            if (nameMatcher.matches()) {
                baseName = nameMatcher.group(1);
            }
            for (int i = 1; scheduleNames.contains(name); ++i) {
                name = String.format("%s - %d", baseName, i);
            }
        }

        schedule.setName(name);
        if ((schedule.getNotes() != null) && (schedule.getNotes().length() > 0)) {
            schedule.setNotes(schedule.getNotes() + "\n\n");
        }
        schedule.setNotes((Optional.of(schedule.getNotes()).orElse(""))
                + String.format("Generated schedule from %s (%s):\n%s", plan.getName(), plan.getSemester(),
                        plan.getNotes()));
        schedule.setSemester(plan.getSemester());

        return schedule;
    }

    private class ScheduleGenerator {
        private static final DayOfWeek[] days = new DayOfWeek[] {
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };
        // private static final PartOfDay[] times = new PartOfDay[] {
        // PartOfDay.MORNING_EARLY, PartOfDay.MORNING_LATE,
        // PartOfDay.AFTERNOON_EARLY, PartOfDay.AFTERNOON_LATE, PartOfDay.EVENING_EARLY,
        // PartOfDay.EVENING_LATE };
        private static final PartOfDay[] times = new PartOfDay[] {
                PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING };

        private CourseOffering[] coursesOffered;
        private Classroom[] classrooms;
        private Instructor[] instructors;
        private InstructorAvailability[] instructorAvailabilities;
        HashMap<String, List<Classroom>> classroomsByType;

        private SemesterPlan semesterPlan;

        private CpModel model = new CpModel();
        private CpSolver solver = new CpSolver();

        private record Slot(
                BoolVar modelVar,
                DayOfWeek dayOfWeek,
                PartOfDay partOfDay,
                Classroom classroom,
                CourseOffering course,
                Instructor instructor) {
        }

        private HashSet<Slot> allSlots;

        public ScheduleGenerator(SemesterPlan plan) {
            Loader.loadNativeLibraries();

            coursesOffered = plan.getCoursesOffered().toArray(CourseOffering[]::new);
            classrooms = plan.getClassroomsAvailable().toArray(Classroom[]::new);
            instructorAvailabilities = plan.getInstructorsAvailable().toArray(InstructorAvailability[]::new);
            instructors = Stream.of(instructorAvailabilities).map(ia -> ia.getInstructor()).distinct()
                    .toArray(Instructor[]::new);
            classroomsByType = makeClassroomsByTypeMap(classrooms);

            semesterPlan = plan;
            prepareModel();
        }

        private HashMap<String, List<Classroom>> makeClassroomsByTypeMap(Classroom[] classrooms) {
            HashMap<String, List<Classroom>> classroomsByType = new HashMap<>();
            for (Classroom classroom : classrooms) {
                classroomsByType.compute(classroom.getRoomType(), (t, list) -> {
                    if (list == null) {
                        list = new ArrayList<Classroom>();
                    }
                    list.add(classroom);
                    return list;
                });
            }
            return classroomsByType;
        }

        private void prepareModel() {
            // For the basic assignment task:
            //
            // There's one variable for each possible assignment of:
            // - a particular course to be taught by
            // - a particular instructor,
            // - in a particular block,
            // - in a particular room.
            //
            // If that instructor is teaching that course at that time, the variable
            // corresponding to that particular assignment has value 1, otherwise 0.
            //
            // Satisfaction of the basic constraint that each course must have a timeslot,
            // instructor, and room are achieved by, for each attribute, summing over all
            // the possible assignments of that attribute and applying an "exactly 1"
            // constraint. Other basic constraints like "an instructor can only be assigned
            // to one course in a particular timeslot" or "two courses can't be taught in
            // the same room at the same time" are mostly modeled by taking sums of
            // different combinations of variables and applying "at most 1" constraints to
            // them.

            logger.info("Generating slots");
            // First, enumerate all our variables
            allSlots = new HashSet<>();
            int nextSlotId = 1;
            for (CourseOffering course : coursesOffered) {
                for (Instructor instructor : course.getApprovedInstructors()) {
                    for (DayOfWeek day : days) {
                        for (PartOfDay time : times) {
                            // TODO add "if time is full block, add full block implies related half block"
                            for (Classroom classroom : classrooms) {// classroomsByType.get(roomType)) {
                                int slotId = nextSlotId++;
                                BoolVar lit = model.newBoolVar(String.format("s%d", slotId));
                                allSlots.add(new Slot(lit, day, time, classroom, course, instructor));
                            }
                        }
                    }
                }
            }

            BiConsumer<Function<Slot, Object>, Consumer<ArrayList<Literal>>> addGroupingConstraint = (
                    groupKeyFn, constraintFn) -> {
                HashMap<Object, ArrayList<Literal>> groupedVars = new HashMap<>();
                for (Slot slot : allSlots) {
                    Object groupKey = groupKeyFn.apply(slot);

                    groupedVars.compute(groupKey, (k, v) -> {
                        if (v == null)
                            v = new ArrayList<>();
                        v.add(slot.modelVar());
                        return v;
                    });
                }
                for (ArrayList<Literal> modelVars : groupedVars.values()) {
                    constraintFn.accept(modelVars);
                }
            };

            long denseSize = (long) days.length * times.length * classrooms.length * instructors.length
                    * coursesOffered.length;
            logger.info("Total slots: {} (dense size {}: {}% reduction from culling)", allSlots.size(), denseSize,
                    (1.0d - ((double) allSlots.size() / (double) denseSize)) * 100d);
            logger.info("Generating constraints");
            // For constraints: "exactly one" by course-block -- make sure every course is
            // being taught, and is only taught once
            // To make multiple block splits work, we would have to make this more complex:
            // instead of "exactly one" slot, we'd be choosing "exactly one pattern"
            addGroupingConstraint.accept(
                    (slot) -> slot.course(),
                    (modelVars) -> model.addExactlyOne(modelVars));

            // For constraints: "at most one" by instructor-block -- don't double-book
            // instructors
            record InstructorBlock(Instructor instructor, DayOfWeek day, PartOfDay time) {
            }
            ;
            addGroupingConstraint.accept(
                    (slot) -> new InstructorBlock(slot.instructor(), slot.dayOfWeek(), slot.partOfDay()),
                    (modelVars) -> model.addAtMostOne(modelVars));

            // For constraints: "at most one" by room-block -- don't double-book rooms
            record ClassroomBlock(Classroom classroom, DayOfWeek day, PartOfDay time) {
            }
            ;
            addGroupingConstraint.accept(
                    // (slotId) -> new ClassroomBlock(slotIdClassroom(slotId), slotIdDay(slotId),
                    // slotIdTime(slotId)),
                    (slot) -> new ClassroomBlock(slot.classroom(), slot.dayOfWeek(), slot.partOfDay()),
                    (modelVars) -> model.addAtMostOne(modelVars));

            // TODO implementation steps
            // Validate the schedule!!!
            // Half blocks
            // - Add "full block implies 2 half blocks" constraints
            // - Make "at most 1" by course-block only check half blocks
            // - Split "exactly 1" by course constraint into "exactly N" by course-blocktype
            // Multiple block layouts
            // - Switch "exactly N" by course-blocktype constraints to logical expressions:
            // -- "M of this and N of that, OR O of this and P of that"
            // Room types
            // - Count rooms by type
            // - Change "at most 1" by course-block constraints to roomtype-block
            // - Change "exactly N" course-blocktype constraints to
            // -- course-blocktype-roomtype
            // - Change room constraint to "at most N" by roomtype-block
            // Optimize
            // - Add variables for instructor allocation by course
            // - Add "any instructor slot occupied equals instructor-course allocation var"
            // . constraint
            // - Add expression for quality (sum of instructor-course allocation times
            // . preference)

            // for (int n : allNurses) {
            // LinearExprBuilder shiftsWorked = LinearExpr.newBuilder();
            // for (int d : allDays) {
            // for (int s : allShifts) {
            // shiftsWorked.add(shifts[n][d][s]);
            // }
            // }
            // model.addLinearConstraint(shiftsWorked, minShiftsPerNurse,
            // maxShiftsPerNurse);
            // }

            solver.getParameters().setLinearizationLevel(0);
            // Tell the solver to enumerate all solutions.
            solver.getParameters().setEnumerateAllSolutions(true);
        }

        public List<Schedule> generate() {
            logger.info("Solving");
            ArrayList<Schedule> schedules = new ArrayList<>();
            CpSolverSolutionCallback solutionCallback = new CpSolverSolutionCallback() {
                static final int maxSolutions = 1;
                int solutionsFound = 0;

                @Override
                public void onSolutionCallback() {
                    // TODO assert that the solution is a real solution!
                    logger.info("Found solution");
                    var assignments = new HashSet<ScheduleAssignment>(coursesOffered.length);
                    for (Slot slot : allSlots) {
                        if (booleanValue(slot.modelVar())) {
                            logger.info("ALLOCATED: {} {} {} {} {}", slot.classroom().getRoomNumber(), slot.dayOfWeek(),
                                    slot.partOfDay(), slot.course().getName(), slot.instructor().getName());
                            assignments.add(new ScheduleAssignment(null, slot.dayOfWeek(),
                                    slot.partOfDay(), slot.classroom(), slot.course(), slot.instructor()));
                        }
                    }
                    ++solutionsFound;
                    if (solutionsFound >= maxSolutions) {
                        stopSearch();
                    }
                    var schedule = new Schedule(null,
                            String.format("Schedule #%d - %s", solutionsFound + 1, semesterPlan.getSemester()),
                            "", semesterPlan.getSemester(), assignments);
                    schedules.add(schedule);
                }
            };

            CpSolverStatus status = solver.solve(model, solutionCallback);
            logger.info("CP Solver status: {}", status);

            return schedules;
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(CourseOfferingController.class);

}
