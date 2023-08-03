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

    private static final Logger logger2 = LoggerFactory.getLogger(GenerateScheduleController.class);

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
            logger2.info("Received request to generate schedule: {}", generateScheduleDto);

            Integer planId = generateScheduleDto.getSemesterPlan().getId();
            SemesterPlan plan = planId != null ? semesterPlanRepository.findById(planId).orElse(null) : null;

            if (plan == null) {
                logger2.warn("Invalid plan ID received: {}", planId);
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

            logger2.info("Generated schedule successfully for plan ID: {}", planId);

            return new ResponseEntity<>(scheduleDto, HttpStatus.OK);
        } catch (Exception e) {
            logger2.error("Error generating schedule", e);
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
                BlockRequirementSplit split,
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

            identDisambiguator = 1;
            identMap = null;
            identStrings = null;
            if (enableModelLog) {
                if (modelLog == null) {
                    modelLog = new StringBuilder(100000);
                }
                modelLog.setLength(0);
                modelLog.append("\n\n\n");
            }

            logger.info("Generating slots");
            // First, enumerate all our variables
            enumerateAllSlots();

            logger.info("Generating constraints");
            addAllGroupingConstraints();

            solver.getParameters().setLinearizationLevel(0);
            // Tell the solver to enumerate all solutions.
            solver.getParameters().setEnumerateAllSolutions(true);

            if (enableModelLog) {
                modelLog.append("\n\n\n");
            }
        }

        public List<Schedule> generate() {
            logger.info("Solving");

            var solutionCallback = new ScheduleSolutionCallback();
            CpSolverStatus status = solver.solve(model, solutionCallback);
            logger.info("CP Solver status: {}", status);

            if (enableModelLog) {
                modelLog.append("\n\n\n");
                logger.info(modelLog.toString());
            }

            if (solutionCallback.getCallbackException() != null) {
                throw new Error(solutionCallback.getCallbackException());
            }

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

        public boolean enableModelLog = false;

        private StringBuilder modelLog = null;
        private ArrayList<IntVar> allVars = new ArrayList<>();
        private HashSet<String> identStrings = null;
        private int identDisambiguator = 1;
        private HashMap<Object, String> identMap = null;

        private String ensafen(String s, int maxLen) {
            StringBuilder b = new StringBuilder(maxLen + 4);
            for (char c : s.toCharArray()) {
                if (b.length() >= maxLen) {
                    break;
                }
                if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                    b.append(c);
                }
            }
            String stem = b.toString();
            String result = stem;
            while (identStrings.contains(result)) {
                result = String.format("%s%d", stem, identDisambiguator++);
            }
            return result;
        }

        private String identify(CourseOffering course) {
            return identify((Object) course, () -> String.format("c%d%s", course.getId(), course.getName()));
        }

        private String identify(Instructor instructor) {
            return identify((Object) instructor,
                    () -> String.format("i%d%s", instructor.getId(), instructor.getName()));
        }

        private String identify(BlockRequirementSplit split) {
            return identify((Object) split, () -> String.format("s%d%s", split.getId(), split.getName()));
        }

        private String identifyRoomType(String roomType) {
            return identify((Object) ("RT:" + roomType), () -> "r" + roomType);
        }

        private String identify(DayOfWeek dayOfWeek) {
            return identMap.get(dayOfWeek);
        }

        private String identify(PartOfDay partOfDay) {
            return identMap.get(partOfDay);
        }

        private <T> String identify(T o, Supplier<String> makeStr) {
            if (identMap == null) {
                identStrings = new HashSet<>(50000);
                identMap = new HashMap<>(50000);

                identStrings.add("");

                identStrings.add("Su");
                identMap.put(DayOfWeek.SUNDAY, "Su");
                identStrings.add("Mo");
                identMap.put(DayOfWeek.MONDAY, "Mo");
                identStrings.add("Tu");
                identMap.put(DayOfWeek.TUESDAY, "Tu");
                identStrings.add("We");
                identMap.put(DayOfWeek.WEDNESDAY, "We");
                identStrings.add("Th");
                identMap.put(DayOfWeek.THURSDAY, "Th");
                identStrings.add("Fr");
                identMap.put(DayOfWeek.FRIDAY, "Fr");
                identStrings.add("Sa");
                identMap.put(DayOfWeek.SATURDAY, "Sa");

                identStrings.add("AM");
                identMap.put(PartOfDay.MORNING, "AM");
                identStrings.add("A1");
                identMap.put(PartOfDay.MORNING_EARLY, "A1");
                identStrings.add("A2");
                identMap.put(PartOfDay.MORNING_LATE, "A2");
                identStrings.add("AM");
                identMap.put(PartOfDay.AFTERNOON, "PM");
                identStrings.add("P1");
                identMap.put(PartOfDay.AFTERNOON_EARLY, "P1");
                identStrings.add("P2");
                identMap.put(PartOfDay.AFTERNOON_LATE, "P2");
                identStrings.add("EV");
                identMap.put(PartOfDay.EVENING, "EV");
                identStrings.add("E1");
                identMap.put(PartOfDay.EVENING_EARLY, "E1");
                identStrings.add("E2");
                identMap.put(PartOfDay.EVENING_LATE, "E2");
            }
            return identMap.computeIfAbsent(o,
                    (k) -> {
                        String s = ensafen(makeStr.get(), 6);
                        identStrings.add(s);
                        return s;
                    });
        }

        private void enumerateAllSlots() {
            allSlots = new HashSet<>();
            HashMap<PartOfDay, Slot> fullBlockSlots = new HashMap<>(fullBlockTimes.length);
            for (CourseOffering course : coursesOffered) {
                for (BlockRequirementSplit split : course.getAllowedBlockSplits()) {
                    for (Instructor instructor : course.getApprovedInstructors()) {
                        // TODO could filter day/times by instructor availability
                        for (DayOfWeek day : days) {
                            for (String roomType : classroomsByType.keySet()) {
                                fullBlockSlots.clear();
                                // In the absence of other constraints, modelVar could reasonably be an IntVar
                                // with bounds [0, classroomsByType.get(roomType).size()]; but we know for sure
                                // that classes won't be scheduled in two rooms at once for any particular
                                // course, because then the students would have to be in two places at once, so
                                // this might as well be a BoolVar.
                                for (PartOfDay time : fullBlockTimes) {
                                    BoolVar modelVar = model.newBoolVar(String.format("blk_%s_%s_%s_%s_%s_%s",
                                            identify(course), identify(split), identify(instructor), identify(day),
                                            identify(time), identifyRoomType(roomType)));
                                    allVars.add(modelVar);
                                    if (enableModelLog) {
                                        modelLog.append(String.format("%s: %s w/ %s @ %s %s in %s\n",
                                                modelVar.getName(), course.getName(), instructor.getName(),
                                                day.toString(), time.toString(), roomType));
                                    }
                                    Slot slot = new Slot(modelVar, day, time, roomType, course, split, instructor);
                                    allSlots.add(slot);
                                    fullBlockSlots.put(time, slot);
                                }
                                for (PartOfDay time : halfBlockTimes) {
                                    BoolVar modelVar = model.newBoolVar(
                                            String.format("blk_%s_%s_%s_%s_%s_%s", identify(course), identify(split),
                                                    identify(instructor), identify(day), identify(time),
                                                    identifyRoomType(roomType)));
                                    allVars.add(modelVar);
                                    if (enableModelLog) {
                                        modelLog.append(String.format("%s: %s w/ %s @ %s %s in %s\n",
                                                modelVar.getName(), course.getName(), instructor.getName(),
                                                day.toString(), time.toString(), roomType));
                                    }
                                    Slot slot = new Slot(modelVar, day, time, roomType, course, split, instructor);
                                    allSlots.add(slot);
                                    // If the full-block slot is allocated, that implies the half-block is
                                    // allocated. Because of this, when checking one-of constraints, we must check
                                    // either the full block or the half block for allocation, but not both, or the
                                    // full block will never be allocated!
                                    Slot fullSlot = fullBlockSlots.get(time.toFull());
                                    model.addImplication(fullSlot.modelVar(), modelVar);
                                    if (enableModelLog) {
                                        modelLog.append(String.format("%s -> %s\n", fullSlot.modelVar().getName(),
                                                modelVar.getName()));
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
            // Consider: prefer earlier instructors on the list
            // Consider: honor preference statements
            // Consider: prefer daytime classes

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
                        ArrayList<BoolVar> splitVars = new ArrayList<>();
                        for (BlockRequirementSplit split : course.getAllowedBlockSplits()) {
                            HashMap<DurationRoomType, Integer> requirements = new HashMap<>();
                            for (BlockRequirement blockReq : split.getBlocks()) {
                                requirements.compute(
                                        new DurationRoomType(blockReq.getDuration(), blockReq.getRoomType()),
                                        (k, count) -> count == null ? 1 : count + 1);
                                if (blockReq.getDuration() == Duration.FULL) {
                                    // A full block will also implicitly occupy 2 half blocks
                                    requirements.compute(
                                            new DurationRoomType(Duration.HALF, blockReq.getRoomType()),
                                            (k, count) -> count == null ? 2 : count + 2);
                                }
                            }

                            BoolVar splitVar = model
                                    .newBoolVar(String.format("spl_%s_%s", identify(course), identify(split)));
                            allVars.add(splitVar);
                            if (enableModelLog) {
                                modelLog.append(String.format("%s: %s split %s\n", splitVar.getName(), course.getName(),
                                        split.getName()));
                            }
                            splitVars.add(splitVar);

                            for (Map.Entry<DurationRoomType, Integer> entry : requirements.entrySet()) {
                                Duration duration = entry.getKey().duration();
                                String roomType = entry.getKey().roomType();
                                // The sum of modelVars tells us how well this we want to include must be
                                // exactly count, i.e. the number of
                                // slots of this type required by this split; the number of excluded slots must
                                // be 0. We achieve that latter by multiplying (via term) the number of excluded
                                // slots by count + 1 -- so if even a single undesired slot is included, it will
                                // put us over the total.
                                Literal[] modelVars = slots.stream()
                                        .filter((slot) -> (slot.partOfDay().getDuration() == duration)
                                                && slot.roomType().equals(roomType))
                                        .map((slot) -> slot.modelVar()).toArray(Literal[]::new);
                                model.addEquality(LinearExpr.term(splitVar, entry.getValue()),
                                        LinearExpr.sum(modelVars));
                                if (enableModelLog) {
                                    modelLog.append(String.format("%s * %d = sum(%s)\n", splitVar.getName(),
                                            slots.size(),
                                            String.join(", ", Arrays.stream(modelVars)
                                                    .map((lit) -> (lit instanceof BoolVar) ? ((BoolVar) lit).getName()
                                                            : "~" + ((BoolVar) lit.not()).getName())
                                                    .toArray(String[]::new))));
                                }
                            }
                        }

                        model.addExactlyOne(splitVars.toArray(Literal[]::new));
                        if (enableModelLog) {
                            modelLog.append(String.format("1 = sum(%s)\n", String.join(", ",
                                    splitVars.stream().map(BoolVar::getName).toArray(String[]::new))));
                        }
                    });

            // For constraints: "at most one" by instructor-block -- don't double-book
            // instructors.
            groupSlotsBy(
                    (slot) -> new Slot(null, slot.dayOfWeek(), slot.partOfDay(), null, null, null,
                            slot.instructor()),
                    (instructorBlock, slots) -> {
                        if (instructorBlock.partOfDay().getDuration() == Duration.HALF) {
                            model.addAtMostOne(modelVarsOf(slots));
                            if (enableModelLog) {
                                modelLog.append(String.format("1 >= sum(%s)\n", String.join(", ", Arrays
                                        .stream(modelVarsOf(slots)).map(BoolVar::getName).toArray(String[]::new))));
                            }
                        }
                    });

            // For constraints: "at most N" (where N is the number of rooms of that
            // particular type) by roomtype-block -- don't allocate more rooms than we have.
            groupSlotsBy(
                    // (slotId) -> new ClassroomBlock(slotIdClassroom(slotId), slotIdDay(slotId),
                    // slotIdTime(slotId)),
                    (slot) -> new Slot(null, slot.dayOfWeek(), slot.partOfDay(), slot.roomType(),
                            null, null, null),
                    (blockRoomType, slots) -> {
                        if (blockRoomType.partOfDay().getDuration() == Duration.HALF) {
                            // Allocated rooms for this roomType must not exceed the number of available
                            // rooms
                            int numRoomsOfType = classroomsByType.get(blockRoomType.roomType()).size();
                            model.addLessOrEqual(LinearExpr.sum(modelVarsOf(slots)), numRoomsOfType);
                            if (enableModelLog) {
                                modelLog.append(String.format("%d >= sum(%s)\n", numRoomsOfType,
                                        String.join(", ", Arrays.stream(modelVarsOf(slots)).map(BoolVar::getName)
                                                .toArray(String[]::new))));
                            }
                        }
                    });
        }

        private BoolVar[] modelVarsOf(List<Slot> slots) {
            return slots.stream().map(Slot::modelVar).toArray(BoolVar[]::new);
        }

        private class ScheduleSolutionCallback extends CpSolverSolutionCallback {
            private static final int maxSolutions = 1;
            private int solutionsFound = 0;
            private ArrayList<Schedule> schedules = new ArrayList<>();

            @Getter
            private Exception callbackException = null;

            public List<Schedule> getSchedules() {
                return schedules;
            }

            @Override
            public void onSolutionCallback() {
                try {
                    callbackException = null;
                    logger.info("Found solution");
                    if (enableModelLog) {
                        modelLog.append("Solution:\n");
                        for (IntVar aVar : allVars) {
                            modelLog.append(String.format("%s := %d\n", aVar.getName(), value(aVar)));
                        }
                    }
                    List<ScheduleAssignment> assignments = assignSpecificRooms();
                    checkForFullCourseAllocation(semesterPlan, assignments);
                    checkForNoExtraCourseAllocation(semesterPlan, assignments);
                    checkForBlockAndRoomConflicts(semesterPlan, assignments);
                    checkForBlockAndRoomTypeSatisfiedExactly(semesterPlan, assignments);
                    checkForBlockAndInstructorConflicts(semesterPlan, assignments);
                    checkForApprovedInstructorSatisfaction(semesterPlan, assignments);
                    checkForNoExtraInstructorAllocation(semesterPlan, assignments);
                    // checkForBlockAndCorequisiteConflicts(semesterPlan, assignments);
                    var schedule = new Schedule(null,
                            String.format("Schedule #%d - %s", solutionsFound + 1, semesterPlan.getSemester()),
                            "", semesterPlan.getSemester(), Set.copyOf(assignments));
                    schedules.add(schedule);
                    if (schedules.size() >= maxSolutions) {
                        stopSearch();
                    }
                } catch (Exception e) {
                    callbackException = e;
                    stopSearch();
                }
            }

            private void checkForFullCourseAllocation(SemesterPlan semesterPlan, List<ScheduleAssignment> assignments) {
                nextCourse: for (CourseOffering course : semesterPlan.getCoursesOffered()) {
                    for (ScheduleAssignment sa : assignments) {
                        if (sa.getCourse().equals(course)) {
                            continue nextCourse;
                        }
                    }
                    throw new IllegalArgumentException(
                            String.format("Course %s (%s) not allocated", course.getName(), course.getCourseNumber()));
                }
            }

            private void checkForNoExtraCourseAllocation(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                for (ScheduleAssignment sa : assignments) {
                    if (!semesterPlan.getCoursesOffered().contains(sa.getCourse())) {
                        throw new IllegalArgumentException(
                                String.format("Course %s (%s) allocated without being offered",
                                        sa.getCourse().getName(), sa.getCourse().getCourseNumber()));
                    }
                }
            }

            private void checkForBlockAndRoomConflicts(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                record BlockClassroom(DayOfWeek dayOfWeek, PartOfDay partOfDay, Classroom classroom) {
                }
                ;

                HashMap<BlockClassroom, ScheduleAssignment> saSlices = new HashMap<>();

                for (ScheduleAssignment sa : assignments) {
                    for (PartOfDay pod : PartOfDay.values()) {
                        if ((pod.getDuration() == Duration.HALF) && pod.conflict(sa.getPartOfDay())) {
                            var saSlice = new BlockClassroom(sa.getDayOfWeek(), pod, sa.getClassroom());
                            if (saSlices.containsKey(saSlice)) {
                                var conflict = saSlices.get(saSlice);
                                throw new IllegalArgumentException(
                                        String.format("Room conflict for %s on %s: %s %s vs. %s %s",
                                                sa.getClassroom().getRoomNumber(), sa.getDayOfWeek(),
                                                sa.getCourse().getName(), sa.getPartOfDay(),
                                                conflict.getCourse().getName(), conflict.getPartOfDay()));
                            }
                            saSlices.put(saSlice, sa);
                        }
                    }
                }
            }

            private void checkForBlockAndRoomTypeSatisfiedExactly(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                nextCourse: for (CourseOffering course : semesterPlan.getCoursesOffered()) {
                    for (BlockRequirementSplit split : course.getAllowedBlockSplits()) {
                        List<BlockRequirement> reqs = new ArrayList<>(split.getBlocks());
                        boolean oversatisfied = false;
                        nextSa: for (ScheduleAssignment sa : assignments) {
                            if (sa.getCourse().equals(course)) {
                                for (int i = 0; i < reqs.size(); ++i) {
                                    BlockRequirement req = reqs.get(i);
                                    if ((req.getDuration() == sa.getPartOfDay().getDuration())
                                            && req.getRoomType().equals(sa.getClassroom().getRoomType())) {
                                        reqs.remove(i);
                                        continue nextSa;
                                    }
                                }
                                oversatisfied = true;
                            }
                        }
                        if ((reqs.size() == 0) && !oversatisfied) {
                            // perfectly satisfied
                            continue nextCourse;
                        }
                    }
                    throw new IllegalArgumentException(
                            String.format("Course %s (%s) block requirements not properly satisfied", course.getName(),
                                    course.getCourseNumber()));
                }
            }

            private void checkForBlockAndInstructorConflicts(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                record BlockInstructor(DayOfWeek dayOfWeek, PartOfDay partOfDay, Instructor instructor) {
                }
                ;

                HashSet<BlockInstructor> saSlices = new HashSet<>();

                for (ScheduleAssignment sa : assignments) {
                    for (PartOfDay pod : PartOfDay.values()) {
                        if ((pod.getDuration() == Duration.HALF) && pod.conflict(sa.getPartOfDay())) {
                            var saSlice = new BlockInstructor(sa.getDayOfWeek(), pod, sa.getInstructor());
                            if (saSlices.contains(saSlice)) {
                                throw new IllegalArgumentException(
                                        String.format("Instructor conflict for %s: %s %s", sa.getInstructor().getName(),
                                                sa.getDayOfWeek(), sa.getPartOfDay()));
                            }
                            saSlices.add(saSlice);
                        }
                    }
                }

            }

            private void checkForNoExtraInstructorAllocation(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                Set<Instructor> instructors = Set.copyOf(semesterPlan.getInstructorsAvailable().stream()
                        .map(InstructorAvailability::getInstructor).distinct().toList());
                for (ScheduleAssignment sa : assignments) {
                    if (!instructors.contains(sa.getInstructor())) {
                        throw new IllegalArgumentException(
                                String.format("Intructor %s allocated without being listed as available",
                                        sa.getInstructor().getName()));
                    }
                }
            }

            private void checkForApprovedInstructorSatisfaction(SemesterPlan semesterPlan,
                    List<ScheduleAssignment> assignments) {
                for (ScheduleAssignment sa : assignments) {
                    if (!sa.getCourse().getApprovedInstructors().contains(sa.getInstructor())) {
                        throw new IllegalArgumentException(
                                String.format("Intructor %s allocated to %s (%s) without being approved",
                                        sa.getInstructor().getName(), sa.getCourse().getName(),
                                        sa.getCourse().getCourseNumber()));
                    }
                }
            }

            private List<ScheduleAssignment> assignSpecificRooms() {
                // TODO This room assignment is a simple enough process, but ideally we would
                // like to be able to do things like prefer to allocate specific rooms to
                // particular instructorsrooms, or keep course sections with multiple
                // allocations in the same room, etc.

                record BlockRoomType(DayOfWeek dayOfWeek, PartOfDay partOfDay, String roomType) {
                }
                ;

                // Cut down the full list of slots to just the ones actually assigned by the
                // solver
                var partialAssignments = new HashMap<BlockRoomType, ArrayList<Slot>>();
                for (Slot slot : allSlots) {
                    if (booleanValue(slot.modelVar())) {
                        partialAssignments.computeIfAbsent(
                                new BlockRoomType(slot.dayOfWeek(), slot.partOfDay(), slot.roomType()),
                                (k) -> new ArrayList<>()).add(slot);
                    }
                }

                var assignments = new ArrayList<ScheduleAssignment>(coursesOffered.length);
                var unallocatedRooms = new HashMap<BlockRoomType, ArrayList<Classroom>>();

                // Allocate rooms for the full blocks, and remember the remaining rooms for half
                // blocks
                for (Map.Entry<BlockRoomType, ArrayList<Slot>> entry : partialAssignments.entrySet()) {
                    if (entry.getKey().partOfDay().getDuration() != Duration.FULL) {
                        continue;
                    }
                    ArrayList<Classroom> classroomsOfType = new ArrayList<>(
                            classroomsByType.get(entry.getKey().roomType()));
                    for (Slot slot : entry.getValue()) {
                        if (classroomsOfType.size() > 0) {
                            Classroom classroom = classroomsOfType.remove(classroomsOfType.size() - 1);
                            assignments.add(new ScheduleAssignment(null, slot.dayOfWeek(),
                                    slot.partOfDay(), classroom, slot.course(), slot.instructor()));
                        } else {
                            logger.info("For course {}: ran out of classrooms for {} {}",
                                    slot.course().getCourseNumber(), slot.dayOfWeek(), slot.partOfDay());
                        }
                    }

                    // Remember the remaining unallocated rooms in the early and late half-blocks --
                    // the early block gets to own the list we made previously, the late one gets a
                    // clone so they can be allocated separately
                    unallocatedRooms.put(new BlockRoomType(entry.getKey().dayOfWeek(),
                            entry.getKey().partOfDay().earlyHalf(), entry.getKey().roomType()), classroomsOfType);
                    unallocatedRooms.put(new BlockRoomType(entry.getKey().dayOfWeek(),
                            entry.getKey().partOfDay().lateHalf(), entry.getKey().roomType()),
                            new ArrayList<>(classroomsOfType));
                }

                // Allocate rooms for half blocks
                for (Map.Entry<BlockRoomType, ArrayList<Slot>> entry : partialAssignments.entrySet()) {
                    if (entry.getKey().partOfDay().getDuration() == Duration.FULL) {
                        continue;
                    }
                    ArrayList<Classroom> classroomsOfType = unallocatedRooms.computeIfAbsent(entry.getKey(),
                            (k) -> new ArrayList<>(classroomsByType.get(k.roomType())));
                    nextSlot: for (Slot slot : entry.getValue()) {
                        BlockRoomType fullBlock = new BlockRoomType(slot.dayOfWeek(), slot.partOfDay().toFull(),
                                slot.roomType());
                        if (partialAssignments.containsKey(fullBlock)) {
                            ArrayList<Slot> fullAssignments = partialAssignments.get(fullBlock);
                            for (Slot fullSlot : fullAssignments) {
                                if ((fullSlot.course().equals(slot.course()))
                                        && (fullSlot.instructor().equals(slot.instructor()))) {
                                    // There's a full block assignment for this time, room type, course, and
                                    // instructor, so this must be just the implied half-slot assignment; we should
                                    // ignore this one since a room has already been selected for the full slot
                                    // assignment.
                                    continue nextSlot;
                                }
                            }
                        }
                        if (classroomsOfType.size() > 0) {
                            Classroom classroom = classroomsOfType.remove(classroomsOfType.size() - 1);
                            assignments.add(new ScheduleAssignment(null, slot.dayOfWeek(),
                                    slot.partOfDay(), classroom, slot.course(), slot.instructor()));
                        } else {
                            logger.info("For course {}: ran out of classrooms for {} {}",
                                    slot.course().getCourseNumber(), slot.dayOfWeek(), slot.partOfDay());
                        }
                    }
                }
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
