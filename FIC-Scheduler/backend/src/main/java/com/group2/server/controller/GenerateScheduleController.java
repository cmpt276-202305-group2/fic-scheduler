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
        private static final PartOfDay[] fullBlockTimes = new PartOfDay[] {
                PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING };
        private static final PartOfDay[] halfBlockTimes = new PartOfDay[] {
                PartOfDay.MORNING_EARLY, PartOfDay.MORNING_LATE, PartOfDay.AFTERNOON_EARLY, PartOfDay.AFTERNOON_LATE,
                PartOfDay.EVENING_EARLY, PartOfDay.EVENING_LATE };

        private CourseOffering[] coursesOffered;
        private Classroom[] classrooms;
        private Instructor[] instructors;
        private InstructorAvailability[] instructorAvailabilities;
        private HashMap<String, List<Classroom>> classroomsByType;

        private SemesterPlan semesterPlan;

        private CpModel model = new CpModel();
        private CpSolver solver = new CpSolver();

        private record Slot(
                BoolVar modelVar,
                DayOfWeek dayOfWeek,
                PartOfDay partOfDay,
                String roomType,
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

        private int nextVarId = Integer.MIN_VALUE;

        private void prepareModel() {
            nextVarId = 1;

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
            enumerateAllSlots();

            logger.info("Generating constraints");
            addAllGroupingConstraints();

            solver.getParameters().setLinearizationLevel(0);
            // Tell the solver to enumerate all solutions.
            solver.getParameters().setEnumerateAllSolutions(true);
        }

        public List<Schedule> generate() {
            logger.info("Solving");

            var solutionCallback = new ScheduleSolutionCallback();
            CpSolverStatus status = solver.solve(model, solutionCallback);
            logger.info("CP Solver status: {}", status);

            if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
                var schedule = solutionCallback.getSchedules().get(0);
                for (ScheduleAssignment assignment : schedule.getAssignments()) {
                    logger.info("ALLOCATED: {} {} {} {} {}", assignment.getClassroom().getRoomNumber(),
                            assignment.getDayOfWeek(), assignment.getPartOfDay(), assignment.getCourse().getName(),
                            assignment.getInstructor().getName());
                }
            }

            return solutionCallback.getSchedules();
        }

        private void enumerateAllSlots() {
            allSlots = new HashSet<>();
            ArrayList<Slot> fullBlockSlots = new ArrayList<>(fullBlockTimes.length);
            for (CourseOffering course : coursesOffered) {
                for (Instructor instructor : course.getApprovedInstructors()) {
                    for (DayOfWeek day : days) {
                        for (String roomType : classroomsByType.keySet()) {// classroomsByType.get(roomType)) {
                            fullBlockSlots.clear();
                            // In the absence of other constraints, modelVar could reasonably be an IntVar
                            // with bounds [0, classroomsByType.get(roomType).size()]; but we know for sure
                            // that classes won't be scheduled in two rooms at once for any particular
                            // course, because then the students would have to be in two places at once, so
                            // this might as well be a BoolVar.
                            for (PartOfDay time : fullBlockTimes) {
                                BoolVar modelVar = model.newBoolVar(String.format("sf%d", nextVarId++));
                                Slot slot = new Slot(modelVar, day, time, roomType, course, instructor);
                                allSlots.add(slot);
                                fullBlockSlots.add(slot);
                            }
                            for (PartOfDay time : halfBlockTimes) {
                                BoolVar modelVar = model.newBoolVar(String.format("sh%d", nextVarId++));
                                Slot slot = new Slot(modelVar, day, time, roomType, course, instructor);
                                allSlots.add(slot);
                                // If the full-block slot is allocated, that implies the half-block is
                                // allocated. Because of this, when checking one-of constraints, we must check
                                // either the full block or the half block for allocation, but not both, or the
                                // full block will never be allocated!
                                for (Slot fullSlot : fullBlockSlots) {
                                    if (fullSlot.partOfDay().conflict(time)) {
                                        model.addImplication(fullSlot.modelVar(), modelVar);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            long denseSize = (long) days.length * halfBlockTimes.length * classrooms.length * instructors.length
                    * coursesOffered.length;
            logger.info("Total slots: {} (dense size {}: {}% reduction from culling)", allSlots.size(), denseSize,
                    (1.0d - ((double) allSlots.size() / (double) denseSize)) * 100d);
        }

        private void addAllGroupingConstraints() {
            // TODO implementation steps
            // Validate the schedule!!!
            // Half blocks
            // + Add "full block implies 2 half blocks" constraints
            // + Make "at most 1" by instructor-block only check half blocks
            // - Split "exactly 1" by course constraint into "exactly N" by
            // .. course-blocktype-roomtype
            // Multiple block layouts
            // - Switch "exactly N" by course-blocktype constraints to logical expressions:
            // -- "M of this and N of that, OR O of this and P of that"
            // Room types
            // + Count rooms by type
            // - Change "at most 1" by course-block constraints to roomtype-block
            // - Change "exactly N" course-blocktype constraints to
            // .. course-blocktype-roomtype
            // + Change room constraint to "at most N" by roomtype-block
            // Optimize
            // - Add variables for instructor allocation by course
            // - Add "any instructor slot occupied equals instructor-course allocation var"
            // .. constraint
            // - Add expression for quality (sum of instructor-course allocation times
            // .. preference)

            // For constraints: "exactly one" by course-block -- make sure every course is
            // being taught, and is only taught once.
            // To make multiple block splits work, we would have to make this more complex:
            // instead of "exactly one" slot, we'd be choosing "exactly one pattern".
            record DurationRoomType(Duration duration, String roomType) {
            }
            ;
            groupSlotsBy(
                    (slot) -> slot.course(),
                    (course, slots) -> {
                        ArrayList<Literal> splitVars = new ArrayList<>();
                        for (BlockRequirementSplit split : course.getAllowedBlockSplits()) {
                            HashMap<DurationRoomType, Integer> requirements = new HashMap<>();
                            for (BlockRequirement blockReq : split.getBlocks()) {
                                requirements.compute(
                                        new DurationRoomType(blockReq.getDuration(), blockReq.getRoomType()),
                                        (k, count) -> count == null ? 1 : count + 1);
                            }

                            Literal splitVar = model.newBoolVar(String.format("split%d", nextVarId++));
                            splitVars.add(splitVar);
                            for (Map.Entry<DurationRoomType, Integer> entry : requirements.entrySet()) {
                                int count = entry.getValue();
                                entry.getKey().duration();
                                entry.getKey().roomType();
                                Literal[] modelVars = slots.stream()
                                        .filter((slot) -> (entry.getKey().equals(
                                                new DurationRoomType(slot.partOfDay().getDuration(), slot.roomType()))))
                                        .map(Slot::modelVar).toArray(Literal[]::new);
                                assert (modelVars.length > 0);
                                model.addEquality(LinearExpr.term(splitVar, count), LinearExpr.sum(modelVars));
                                logger.info("For course {}/{}[{},{}]: sum([{}]) = {}", course.getCourseNumber(),
                                        nextVarId, entry.getKey().duration(), entry.getKey().roomType(),
                                        modelVars.length, entry.getValue());
                            }
                        }

                        logger.info("For course {}/{}: sum([{}]) = 1", course.getCourseNumber(), nextVarId,
                                splitVars.size());
                        model.addExactlyOne(splitVars.toArray(Literal[]::new));
                    });

            // For constraints: "at most one" by instructor-block -- don't double-book
            // instructors.
            groupSlotsBy(
                    (slot) -> new Slot(null, slot.dayOfWeek(), slot.partOfDay(), null, null,
                            slot.instructor()),
                    (instructorBlock, slots) -> {
                        if (instructorBlock.partOfDay().getDuration() == Duration.HALF) {
                            model.addAtMostOne(modelVarsOf(slots));
                        }
                    });

            // For constraints: "at most N" (where N is the number of rooms of that
            // particular type) by roomtype-block -- don't allocate more rooms than we have.
            groupSlotsBy(
                    // (slotId) -> new ClassroomBlock(slotIdClassroom(slotId), slotIdDay(slotId),
                    // slotIdTime(slotId)),
                    (slot) -> new Slot(null, slot.dayOfWeek(), slot.partOfDay(), slot.roomType(),
                            null, null),
                    (blockRoomType, slots) -> {
                        if (blockRoomType.partOfDay().getDuration() == Duration.HALF) {
                            // Allocated rooms for this roomType must not exceed the number of available
                            // rooms
                            int numRoomsOfType = classroomsByType.get(blockRoomType.roomType()).size();
                            model.addLessOrEqual(LinearExpr.sum(modelVarsOf(slots)), numRoomsOfType);
                        }
                    });
        }

        private Literal[] modelVarsOf(List<Slot> slots) {
            return slots.stream().map(Slot::modelVar).toArray(Literal[]::new);
        }

        private class ScheduleSolutionCallback extends CpSolverSolutionCallback {
            private static final int maxSolutions = 1;
            private int solutionsFound = 0;
            private ArrayList<Schedule> schedules = new ArrayList<>();

            public List<Schedule> getSchedules() {
                return schedules;
            }

            @Override
            public void onSolutionCallback() {
                // TODO assert that the solution is a real solution!
                logger.info("Found solution");
                HashSet<ScheduleAssignment> assignments = assignSpecificRooms();
                var schedule = new Schedule(null,
                        String.format("Schedule #%d - %s", solutionsFound + 1, semesterPlan.getSemester()),
                        "", semesterPlan.getSemester(), assignments);
                schedules.add(schedule);
                if (schedules.size() >= maxSolutions) {
                    stopSearch();
                }
            }

            private HashSet<ScheduleAssignment> assignSpecificRooms() {
                var assignments = new HashSet<ScheduleAssignment>(coursesOffered.length);
                groupSlotsBy(
                        (slot) -> new Slot(null, slot.dayOfWeek(), slot.partOfDay(), slot.roomType(), null, null),
                        (key, slots) -> {
                            // TODO try to make the allocation stable -- i.e. if a class has multiple blocks
                            // with the same room type try to allocate them to the same actual room
                            var roomsOfType = classroomsByType.get(key.roomType());
                            int i = 0;
                            for (Slot slot : slots) {
                                if (booleanValue(slot.modelVar())) {
                                    if (i < roomsOfType.size()) {
                                        // constraints should have ensured this
                                        Classroom classroom = roomsOfType.get(i++);
                                        assignments.add(new ScheduleAssignment(null, slot.dayOfWeek(),
                                                slot.partOfDay(), classroom, slot.course(), slot.instructor()));
                                    } else {
                                        logger.info("For course {}: ran out of classrooms for {} {}",
                                                slot.course().getCourseNumber(), slot.dayOfWeek(), slot.partOfDay());
                                    }
                                }
                            }
                        });
                return assignments;
            }
        };

        private <T> void groupSlotsBy(Function<Slot, T> groupKeyFn,
                BiConsumer<T, ArrayList<Slot>> constraintFn) {
            HashMap<T, ArrayList<Slot>> groupedVars = new HashMap<>();
            for (Slot slot : allSlots) {
                T groupKey = groupKeyFn.apply(slot);

                groupedVars.compute(groupKey, (k, v) -> {
                    if (v == null)
                        v = new ArrayList<>();
                    v.add(slot);
                    return v;
                });
            }
            for (Map.Entry<T, ArrayList<Slot>> entry : groupedVars.entrySet()) {
                constraintFn.accept(entry.getKey(), entry.getValue());
            }
        };

    }

    private static final Logger logger = LoggerFactory.getLogger(CourseOfferingController.class);

}
