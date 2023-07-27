package com.group2.server.controller;

import java.util.*;
import java.util.regex.*;
// import java.util.stream.*;

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
        @Data
        @AllArgsConstructor
        private static class Block {
            private DayOfWeek dayOfWeek;
            private PartOfDay partOfDay;
        }

        @Data
        @AllArgsConstructor
        private static class Slot {
            private Literal slotAllocated;
            private DayOfWeek dayOfWeek;
            private PartOfDay partOfDay;
            private Classroom classroom;
            private CourseOffering course;
            private Instructor instructor;
        }

        // private static final DayOfWeek[] daysOfWeek = new DayOfWeek[] {
        // DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
        // DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };
        // private static final PartOfDay[] partsOfDay = new PartOfDay[] {
        // PartOfDay.MORNING_EARLY, PartOfDay.MORNING_LATE,
        // PartOfDay.AFTERNOON_EARLY, PartOfDay.AFTERNOON_LATE, PartOfDay.EVENING_EARLY,
        // PartOfDay.EVENING_LATE };
        // private static final Block[] blocks = Stream.of(daysOfWeek)
        // .flatMap(d -> Stream.of(partsOfDay).map(p -> new Block(d,
        // p))).toArray(Block[]::new);

        private CourseOffering[] coursesOffered;
        // private Classroom[] classrooms;
        // private InstructorAvailability[] instructorAvailabilities;
        // HashMap<String, List<Classroom>> classroomsByType;

        CpModel model = new CpModel();
        CpSolver solver = new CpSolver();

        private ArrayList<Slot> allSlots = new ArrayList<>();

        public ScheduleGenerator(SemesterPlan plan) {
            Loader.loadNativeLibraries();

            coursesOffered = plan.getCoursesOffered().toArray(CourseOffering[]::new);
            // classrooms = plan.getClassroomsAvailable().toArray(Classroom[]::new);
            // instructorAvailabilities =
            // plan.getInstructorsAvailable().toArray(InstructorAvailability[]::new);
            // classroomsByType = makeClassroomsByTypeMap(classrooms);

            prepareModel();
        }

        // private HashMap<String, List<Classroom>> makeClassroomsByTypeMap(Classroom[]
        // classrooms) {
        // HashMap<String, List<Classroom>> classroomsByType = new HashMap<>();
        // for (Classroom classroom : classrooms) {
        // classroomsByType.compute(classroom.getRoomType(), (t, list) -> {
        // if (list == null) {
        // list = new ArrayList<Classroom>();
        // }
        // list.add(classroom);
        // return list;
        // });
        // }
        // return classroomsByType;
        // }

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

            // Set of constraints: "exactly one" by course
            HashMap<Integer, ArrayList<Literal>> variablesByCourse = new HashMap<>();
            // HashMap<Integer, List<Literal>> variablesByBlock;
            // Set of constraints: "at most one" by instructor-block
            HashMap<Integer, ArrayList<Literal>> variablesByInstructorBlock = new HashMap<>();
            // Set of constraints: "at most one" by room-block
            // HashMap<Integer, List<Literal>> variablesByClassroom;

            // Literal[] lits = new Literal[] { model.newBoolVar("a"),
            // model.newBoolVar("b"),
            // model.newBoolVar("c"),
            // model.newBoolVar("d") };
            // model.addExactlyOne(lits);
            // model.addAtMostOne(new Literal[] { lits[0], lits[1] });
            // model.addAtMostOne(new Literal[] { lits[2], lits[3] });
            // for (Literal lit : lits) {
            // allSlots.add(
            // new Slot(lit, DayOfWeek.MONDAY, PartOfDay.MORNING,
            // new Classroom(null, "room" + lit.getIndex(), "Small", ""),
            // new CourseOffering(null, "CMPT100/O1", "CMPT100", "", Set.of(), Set.of()),
            // new Instructor(null, "instructor", "")));
            // }

            Classroom classroom = new Classroom(null, "The Room", "Small", "");
            for (CourseOffering course : coursesOffered) {
                ArrayList<Literal> courseVars = variablesByCourse.compute(course.getId(),
                        (k, v) -> v != null ? v : new ArrayList<>());
                for (Instructor instructor : course.getApprovedInstructors()) {
                    for (int block = 0; block < 5; ++block) {
                        int instructorBlockId = instructor.getId() * 5 + block;
                        ArrayList<Literal> instructorVars = variablesByInstructorBlock.compute(instructorBlockId,
                                (k, v) -> v != null ? v : new ArrayList<>());
                        Literal lit = model.newBoolVar(String.format("b%di%dc%d", block, instructor.getId(),
                                course.getId()));
                        courseVars.add(lit);
                        instructorVars.add(lit);
                        allSlots.add(new Slot(lit, DayOfWeek.values()[block], PartOfDay.MORNING, classroom, course,
                                instructor));
                    }
                    // for (BlockRequirementSplit blockReqSplit : course.getAllowedBlockSplits())
                    // {
                    // for (BlockRequirement blockReq : blockReqSplit.getBlocks()) {
                    // // for (String roomType : blockReq.getAllowedRoomTypes()) { for (Classroom
                    // // classroom :...
                    // switch (blockReq.getDuration()) {
                    // case HALF: {
                    // break;
                    // }
                    // case FULL: {
                    // break;
                    // }
                    // }
                    // }
                    // }
                }
            }

            // Each course must be assigned in exactly one slot
            for (ArrayList<Literal> variables : variablesByCourse.values()) {
                logger.info("Course variables: {}",
                        String.join(", ", variables.stream().map(Literal::toString).toList()));
                model.addExactlyOne(variables);
            }
            // Each instructor must be assigned at most one class per blokc
            for (ArrayList<Literal> variables : variablesByInstructorBlock.values()) {
                logger.info("Instructor variables: {}",
                        String.join(", ", variables.stream().map(Literal::toString).toList()));
                model.addAtMostOne(variables);
            }
            // May need a "max classes per instructor" constraint

            // // Try to distribute the shifts evenly, so that each nurse works
            // // minShiftsPerNurse shifts. If this is not possible, because the total
            // // number of shifts is not divisible by the number of nurses, some nurses
            // will
            // // be assigned one more shift.
            // int minShiftsPerNurse = (numShifts * numDays) / numNurses;
            // int maxShiftsPerNurse;
            // if ((numShifts * numDays) % numNurses == 0) {
            // maxShiftsPerNurse = minShiftsPerNurse;
            // } else {
            // maxShiftsPerNurse = minShiftsPerNurse + 1;
            // }
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
            ArrayList<Schedule> schedules = new ArrayList<>();
            CpSolverSolutionCallback solutionCallback = new CpSolverSolutionCallback() {
                static final int maxSolutions = 10;
                int solutionsFound = 0;

                // for (var course : plan.getCoursesOffered()) {
                // var dayOfWeek = daysOfWeek[r.nextInt(partsOfDay.length)];
                // var partOfDay = partsOfDay[r.nextInt(partsOfDay.length)];
                // var classroom = classrooms[r.nextInt(classrooms.length)];
                // var instructor =
                // instructorAvailabilities[r.nextInt(instructorAvailabilities.length)].getInstructor();
                // assignments.add(new ScheduleAssignment(null, dayOfWeek, partOfDay, classroom,
                // course, instructor));
                // }
                // schedule.setAssignments(assignments);
                // schedule = scheduleRepository.save(schedule);
                @Override
                public void onSolutionCallback() {
                    logger.info("Found solution");
                    for (Slot slot : allSlots) {
                        if (booleanValue(slot.getSlotAllocated())) {
                            logger.info("ALLOCATED: {} {} {} {} {}", slot.getClassroom().getRoomNumber(),
                                    slot.getDayOfWeek(), slot.getPartOfDay(), slot.getCourse().getName(),
                                    slot.getInstructor().getName());
                        }
                    }
                    ++solutionsFound;
                    if (solutionsFound >= maxSolutions) {
                        stopSearch();
                    }
                }
            };

            CpSolverStatus status = solver.solve(model, solutionCallback);
            logger.info("CP Solver status: {}", status);
            // CpSolverStatus.FEASIBLE?

            return schedules;
        }

    }

    private static final Logger logger = LoggerFactory.getLogger(CourseOfferingController.class);

}
