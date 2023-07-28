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
        private Integer[] instructorIds;
        private InstructorAvailability[] instructorAvailabilities;
        HashMap<String, List<Classroom>> classroomsByType;

        CpModel model = new CpModel();
        CpSolver solver = new CpSolver();

        private record Slot(
                BoolVar modelVar,
                DayOfWeek dayOfWeek,
                PartOfDay partOfDay,
                Classroom classroom,
                CourseOffering course,
                Instructor instructor) {
        }

        private HashMap<Integer, Slot> allSlots;

        public ScheduleGenerator(SemesterPlan plan) {
            Loader.loadNativeLibraries();

            coursesOffered = plan.getCoursesOffered().toArray(CourseOffering[]::new);
            classrooms = plan.getClassroomsAvailable().toArray(Classroom[]::new);
            instructorAvailabilities = plan.getInstructorsAvailable().toArray(InstructorAvailability[]::new);
            instructorIds = Stream.of(instructorAvailabilities).map(ia -> ia.getInstructor().getId()).distinct()
                    .toArray(Integer[]::new);
            classroomsByType = makeClassroomsByTypeMap(classrooms);

            // prepareSlotIds();
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

        // If Java had tuples built in I probably wouldn't have gone to all this trouble

        // private static final int daySlotIdShift = 0;
        // private static final int daySlotIdSize = Integer.SIZE -
        // Integer.numberOfLeadingZeros(DayOfWeek.values().length);
        // private static final int daySlotIdMask = ((1 << daySlotIdSize) - 1) <<
        // daySlotIdShift;
        // private static final int timeSlotIdShift = daySlotIdShift + daySlotIdSize;
        // private static final int timeSlotIdSize = Integer.SIZE
        // - Integer.numberOfLeadingZeros(PartOfDay.values().length);
        // private static final int timeSlotIdMask = ((1 << timeSlotIdSize) - 1) <<
        // timeSlotIdShift;

        // private static final int courseSlotIdShift = timeSlotIdShift +
        // timeSlotIdSize;
        // private int courseSlotIdSize;
        // private int courseSlotIdMask;

        // private int instructorSlotIdShift;
        // private int instructorSlotIdSize;
        // private int instructorSlotIdMask;
        // private int classroomSlotIdShift;
        // private int classroomSlotIdSize;
        // private int classroomSlotIdMask;

        // private int slotIdSize;
        // private int denseSlotIdArraySize;

        // private void prepareSlotIds() {
        // courseSlotIdSize = Integer.SIZE -
        // Integer.numberOfLeadingZeros(coursesOffered.length);
        // courseSlotIdMask = ((1 << courseSlotIdSize) - 1) << courseSlotIdShift;
        // instructorSlotIdShift = courseSlotIdShift + courseSlotIdSize;
        // instructorSlotIdSize = Integer.SIZE -
        // Integer.numberOfLeadingZeros(instructorIds.length);
        // instructorSlotIdMask = ((1 << instructorSlotIdSize) - 1) <<
        // instructorSlotIdShift;
        // classroomSlotIdShift = instructorSlotIdShift + instructorSlotIdSize;
        // classroomSlotIdSize = Integer.SIZE -
        // Integer.numberOfLeadingZeros(classrooms.length);
        // classroomSlotIdMask = ((1 << classroomSlotIdSize) - 1) <<
        // classroomSlotIdShift;
        // slotIdSize = classroomSlotIdShift + classroomSlotIdSize;
        // denseSlotIdArraySize = 1 << slotIdSize;
        // }

        // private int daySlotId(DayOfWeek day) {
        // return day.ordinal() << daySlotIdShift;
        // }

        // private int timeSlotId(PartOfDay time) {
        // return time.ordinal() << timeSlotIdShift;
        // }

        // private int courseSlotId(CourseOffering course) {
        // int findId = course.getId();
        // for (int i = 0; i < coursesOffered.length; ++i) {
        // if (findId == coursesOffered[i].getId()) {
        // return i << courseSlotIdShift;
        // }
        // }
        // return -1;
        // }

        // private int instructorSlotId(Instructor instructor) {
        // int findId = instructor.getId();
        // for (int i = 0; i < coursesOffered.length; ++i) {
        // if (findId == instructorIds[i]) {
        // return i << instructorSlotIdShift;
        // }
        // }
        // return -1;
        // }

        // private int classroomSlotId(Classroom classroom) {
        // int findId = classroom.getId();
        // for (int i = 0; i < classrooms.length; ++i) {
        // if (findId == classrooms[i].getId()) {
        // return i << classroomSlotIdShift;
        // }
        // }
        // return -1;
        // }

        // private int slotIdOf(CourseOffering course, DayOfWeek day, PartOfDay time,
        // Instructor instructor,
        // Classroom classroom) {
        // return daySlotId(day) | timeSlotId(time) | courseSlotId(course) |
        // instructorSlotId(instructor)
        // | classroomSlotId(classroom);
        // }

        // private CourseOffering slotIdCourse(int slotId) {
        // return coursesOffered[(slotId & courseSlotIdMask) >> courseSlotIdShift];
        // }

        // private Integer slotIdInstructorId(int slotId) {
        // return instructorIds[(slotId & instructorSlotIdMask) >>
        // instructorSlotIdShift];
        // }

        // private Classroom slotIdClassroom(int slotId) {
        // return classrooms[(slotId & classroomSlotIdMask) >> classroomSlotIdShift];
        // }

        // private DayOfWeek slotIdDay(int slotId) {
        // return DayOfWeek.values()[(slotId & daySlotIdMask) >> daySlotIdShift];
        // }

        // private PartOfDay slotIdTime(int slotId) {
        // return PartOfDay.values()[(slotId & timeSlotIdMask) >> timeSlotIdShift];
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

            // HashMap<Integer, ArrayList<>>
            // HashMap<Integer, ArrayList<RoomType>>
            // for (BlockRequirementSplit blockReqSplit :
            // blockRequirementSplitRepository.findAll()) {
            // for (BlockRequirement blockReq : blockReqSplit.getBlocks()) {
            // for (String roomType : blockReq.getAllowedRoomTypes()) {
            // }
            // }
            // }

            logger.info("Generating slots");
            // First, enumerate all our variables
            allSlots = new HashMap<>();
            int nextSlotId = 1;
            for (CourseOffering course : coursesOffered) {
                for (Instructor instructor : course.getApprovedInstructors()) {
                    for (DayOfWeek day : days) {
                        for (PartOfDay time : times) {
                            for (Classroom classroom : classrooms) {// classroomsByType.get(roomType)) {
                                // int slotId = slotIdOf(course, day, time, instructor, classroom);
                                int slotId = nextSlotId++;
                                BoolVar lit = model.newBoolVar(String.format("s%08x", slotId));
                                allSlots.put(slotId, new Slot(lit, day, time, classroom, course, instructor));
                            }
                        }
                    }
                }
            }

            // BiConsumer<Function<Integer, Object>, Consumer<ArrayList<Literal>>>
            // addGroupingConstraint = (
            // groupKeyFn, constraintFn) -> {
            BiConsumer<Function<Slot, Object>, Consumer<ArrayList<Literal>>> addGroupingConstraint = (
                    groupKeyFn, constraintFn) -> {
                HashMap<Object, ArrayList<Literal>> groupedVars = new HashMap<>();
                for (Map.Entry<Integer, Slot> entry : allSlots.entrySet()) {
                    // int slotId = entry.getKey();
                    // Object groupKey = groupKeyFn.apply(slotId);
                    Object groupKey = groupKeyFn.apply(entry.getValue());

                    groupedVars.compute(groupKey, (k, v) -> {
                        if (v == null)
                            v = new ArrayList<>();
                        v.add(entry.getValue().modelVar());
                        return v;
                    });
                }
                for (ArrayList<Literal> modelVars : groupedVars.values()) {
                    constraintFn.accept(modelVars);
                }
            };

            long denseSize = (long) days.length * times.length * classrooms.length * instructorIds.length
                    * coursesOffered.length;
            logger.info("Total slots: {} (dense size {}: {}% reduction from culling)", allSlots.size(), denseSize,
                    (1.0d - ((double) allSlots.size() / (double) denseSize) * 100d));
            logger.info("Generating constraints");
            // For constraints: "exactly one" by course-block -- make sure every course is
            // being taught, and is only taught once
            // To make multiple block splits work, we would have to make this more complex:
            // instead of "exactly one" slot, we'd be choosing "exactly one pattern"
            addGroupingConstraint.accept(
                    // (slotId) -> slotIdCourse(slotId),
                    (slot) -> slot.course(),
                    (modelVars) -> model.addExactlyOne(modelVars));

            // For constraints: "at most one" by instructor-block -- don't double-book
            // instructors
            record InstructorBlock(Integer instructorId, DayOfWeek day, PartOfDay time) {
            }
            ;
            addGroupingConstraint.accept(
                    // (slotId) -> new InstructorBlock(slotIdInstructorId(slotId),
                    // slotIdDay(slotId), slotIdTime(slotId)),
                    (slot) -> new InstructorBlock(slot.instructor().getId(), slot.dayOfWeek(), slot.partOfDay()),
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
            logger.info("Solving");
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
                    // TODO assert that the solution is a real solution!
                    logger.info("Found solution");
                    for (Slot slot : allSlots.values()) {
                        if (booleanValue(slot.modelVar())) {
                            logger.info("ALLOCATED: {} {} {} {} {}", slot.classroom().getRoomNumber(), slot.dayOfWeek(),
                                    slot.partOfDay(), slot.course().getName(), slot.instructor().getName());
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
