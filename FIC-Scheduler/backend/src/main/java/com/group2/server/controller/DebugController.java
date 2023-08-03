package com.group2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// import com.group2.server.dto.*;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import lombok.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
@CrossOrigin("*")
public class DebugController {
    @Autowired
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    @Autowired
    private ClassroomRepository classroomRepository;

    @Autowired
    private CourseOfferingRepository courseOfferingRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    private static final String roomTypeClassroom = "Classroom";
    private static final String roomTypeScienceLab = "Science Lab";
    private static final String roomTypeComputerLab = "Computer Lab";
    private static final String[] firstNames = { "Olivia", "Sophia", "Amelia", "Emma", "Ava", "Charlotte", "Lily",
            "Hannah", "Nora", "Isabella", "Noah", "Liam", "Jackson", "Oliver", "Leo", "Lucas", "Luca", "Jack", "James",
            "Benjamin" };
    private static final String[] lastNames = { "Tremblay", "Roy", "Gagnon", "MacDonald", "Leblanc", "Cote", "Gauthier",
            "Smith", "Johnson", "Williams", "Brown", "Jones", "Miller", "Davis", "Garcia", "Rodriguez", "Wilson",
            "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", "Moore", "Martin", "Jackson", "Thompson", "White",
            "Lopez", "Lee", "Gonzalez", "Harris", "Clark", "Lewis", "Robinson", "Walker", "Perez", "Hall", "Young",
            "Allen", "Sanchez", "Wright", "King", "Scott", "Green", "Baker", "Adams", "Nelson", "Hill", "Ramirez",
            "Campbell", "Mitchell", "Roberts", "Carter", "Phillips", "Evans", "Turner", "Torres", "Parker", "Collins",
            "Edwards", "Stewart", "Flores", "Morris", "Nguyen", "Murphy", "Rivera", "Cook", "Rogers", "Morgan",
            "Peterson", "Cooper", "Reed", "Bailey", "Bell", "Gomez", "Kelly", "Howard", "Ward", "Cox", "Diaz",
            "Richardson", "Wood", "Watson", "Brooks", "Bennett", "Gray", "James", "Reyes", "Cruz", "Hughes", "Price",
            "Myers", "Long", "Foster", "Sanders", "Ross", "Morales", "Powell", "Sullivan", "Russell", "Ortiz",
            "Jenkins", "Gutierrez", "Perry", "Butler", "Barnes", "Fisher" };
    private static final String[] fullNames = new String[60];

    @Data
    @AllArgsConstructor
    private static class BlockRequirementTemplate {
        private String roomType;
        private Duration duration;
    }

    private static final String halfPeriodClassroom = "Half period/classroom";
    private static final String fullPeriodClassroom = "Full period/classroom";
    private static final String fullPeriodClassroomAndHalfPeriodComputerLab = "Full period/classroom + half period/computer lab";
    private static final String fullPeriodClassroomAndFullPeriodScienceLab = "Full period/classroom + full period/science lab";
    private static final Map<String, List<BlockRequirementTemplate>> blockRequirements = Map.ofEntries(
            Map.entry(halfPeriodClassroom,
                    List.of(new BlockRequirementTemplate(roomTypeClassroom, Duration.HALF))),
            Map.entry(fullPeriodClassroom,
                    List.of(new BlockRequirementTemplate(roomTypeClassroom, Duration.FULL))),
            Map.entry(fullPeriodClassroomAndHalfPeriodComputerLab,
                    List.of(new BlockRequirementTemplate(roomTypeClassroom, Duration.FULL),
                            new BlockRequirementTemplate(roomTypeComputerLab, Duration.HALF))),
            Map.entry(fullPeriodClassroomAndFullPeriodScienceLab,
                    List.of(new BlockRequirementTemplate(roomTypeClassroom, Duration.FULL),
                            new BlockRequirementTemplate(roomTypeScienceLab, Duration.FULL))));
    private static final Map<String, String> classrooms = Map.ofEntries(
            Map.entry("DISC1 2400", roomTypeClassroom),
            Map.entry("DISC1 2420", roomTypeClassroom),
            Map.entry("DISC1 2440", roomTypeClassroom),
            Map.entry("DISC1 2460 (C)", roomTypeComputerLab),
            Map.entry("DISC1 2480 (C)", roomTypeComputerLab),
            Map.entry("DISC1 2500", roomTypeClassroom),
            Map.entry("DISC1 2520", roomTypeClassroom),
            Map.entry("DISC1 2540", roomTypeClassroom),
            Map.entry("DISC1 2560", roomTypeClassroom),
            Map.entry("DISC1 2580 (S)", roomTypeScienceLab),
            Map.entry("DISC1 3420", roomTypeClassroom),
            Map.entry("DISC1 3440", roomTypeClassroom),
            Map.entry("DISC1 3460", roomTypeClassroom),
            Map.entry("DISC1 3480", roomTypeClassroom),
            Map.entry("DISC1 3520", roomTypeClassroom),
            Map.entry("DISC1 3540", roomTypeClassroom),
            Map.entry("DISC1 3560", roomTypeClassroom));
    private static final String[] subjects = { "CMPT", "PHYS", "ENGL", "BUS", "POL", "ECON", "ILS", "ALC" };

    static {
        for (int i = 0; i < fullNames.length; ++i) {
            var r = new Random(13);
            for (i = 0; i < fullNames.length;) {
                String fullName = firstNames[r.nextInt(firstNames.length)] + " "
                        + lastNames[r.nextInt(lastNames.length)];
                for (int j = 0; j < i; ++j) {
                    if (fullName.equals(fullNames[j])) {
                        continue;
                    }
                }
                fullNames[i] = fullName;
                ++i;
            }
        }
    }

    @PostMapping("/populate-test-blocks")
    public ResponseEntity<Void> populateTestBlocks() {
        try {
            for (Entry<String, List<BlockRequirementTemplate>> e : blockRequirements.entrySet()) {
                if (blockRequirementSplitRepository.findByName(e.getKey()).size() == 0) {
                    blockRequirementSplitRepository.save(new BlockRequirementSplit(null, e.getKey(),
                            e.getValue().stream().map(
                                    brt -> new BlockRequirement(null, brt.getRoomType(), brt.getDuration()))
                                    .toList()));
                }
            }

            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/populate-test-classrooms")
    public ResponseEntity<Void> populateTestClassrooms() {
        try {
            for (Entry<String, String> e : classrooms.entrySet()) {
                if (classroomRepository.findByRoomNumber(e.getKey()).size() == 0) {
                    classroomRepository.save(new Classroom(null, e.getKey(), e.getValue(),
                            "Created by DebugController.populateTestClassrooms"));
                }
            }
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/populate-test-course-offerings")
    public ResponseEntity<Void> populateTestCourseOfferings() {
        ResponseEntity<Void> result;
        result = populateTestBlocks();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        result = populateTestInstructors();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        try {
            var r = new Random(40);
            ArrayList<Instructor> instructors = new ArrayList<>(instructorRepository.findAll().stream().toList());
            var ilsAllowedBlockSplits = blockRequirementSplitRepository.findByName(halfPeriodClassroom);
            var cmptLabAllowedBlockSplits = blockRequirementSplitRepository
                    .findByName(fullPeriodClassroomAndHalfPeriodComputerLab);
            var physLabAllowedBlockSplits = blockRequirementSplitRepository
                    .findByName(fullPeriodClassroomAndFullPeriodScienceLab);
            var genericAllowedBlockSplits = blockRequirementSplitRepository.findByName(fullPeriodClassroom);

            for (var subject : subjects) {
                // Shuffle instructor priorities for the subject
                Collections.shuffle(instructors, r);

                var numCourses = subject.equals("ILS") || subject.equals("ALC") ? 1 : 6;
                var numSections = 4;
                var numberPart = subject.equals("ILS") ? 101 : (subject.equals("ALC") ? 99 : 100);

                for (int i = 0; i < numCourses; ++i) {
                    int minApprovedInstructors = Math.min((numSections + 1) / 2, instructors.size());
                    int maxApprovedInstructors = Math.min(minApprovedInstructors + numSections, instructors.size());
                    ArrayList<Instructor> approvedInstructors = new ArrayList<>(maxApprovedInstructors);
                    for (int j = 0; (j < instructors.size())
                            && (approvedInstructors.size() < maxApprovedInstructors); ++j) {
                        if (r.nextBoolean()) {
                            approvedInstructors.add(instructors.get(j));
                        }
                    }
                    if (approvedInstructors.size() < minApprovedInstructors) {
                        approvedInstructors.clear();
                        for (int j = 0; j < minApprovedInstructors; ++j) {
                            approvedInstructors.add(instructors.get(j));
                        }
                    }

                    Set<BlockRequirementSplit> allowedBlockSplits = null;
                    if (subject.equals("ILS")) {
                        allowedBlockSplits = ilsAllowedBlockSplits;
                        // ALC099 halfPeriodClassroom
                    } else if (subject.equals("CMPT") && ((i == 1) || (i == 2))) {
                        allowedBlockSplits = cmptLabAllowedBlockSplits;
                    } else if (subject.equals("PHYS") && ((i == 1) || (i == 2))) {
                        allowedBlockSplits = physLabAllowedBlockSplits;
                    } else {
                        allowedBlockSplits = genericAllowedBlockSplits;
                    }
                    for (int section = 0; section < numSections; ++section) {
                        var name = String.format("%s%03d/O%d", subject, numberPart, section);
                        var courseNumber = String.format("%s%03d", subject, numberPart);
                        if (courseOfferingRepository.findByName(name).size() > 0)
                            continue;
                        courseOfferingRepository.save(new CourseOffering(null,
                                name,
                                courseNumber,
                                "Created by DebugController.populateTestCourseOfferings",
                                Set.copyOf(approvedInstructors),
                                allowedBlockSplits));
                    }
                    numberPart += 5 * (r.nextInt(4) + 1);
                    if (numSections > 1)
                        --numSections;
                }
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/populate-test-instructors")
    public ResponseEntity<Void> populateTestInstructors() {
        try {
            var usedNames = new HashSet<String>();
            usedNames.addAll(instructorRepository.findAll().stream().map(i -> i.getName()).toList());
            for (String name : fullNames) {
                if (!usedNames.contains(name)) {
                    usedNames.add(name);
                    instructorRepository
                            .save(new Instructor(null, name, "Created by DebugController.populateTestInstructors"));
                }
            }
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/populate-test-semester-plan")
    public ResponseEntity<Void> populateTestSemesterPlan() {
        ResponseEntity<Void> result;
        result = populateTestBlocks();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        result = populateTestClassrooms();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        result = populateTestCourseOfferings();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        result = populateTestInstructors();
        if (result.getStatusCode() != HttpStatus.OK)
            return result;
        try {
            var r = new Random(104);
            final String name = "Debug Semester Plan";
            final String semester = "Fall 2023";
            final DayOfWeek[] weekDays = { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY };
            final PartOfDay[] partsOfDay = { PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING };
            if (semesterPlanRepository.findBySemester(semester).size() == 0) {
                var courseOfferings = Set.copyOf(courseOfferingRepository.findAll());
                var classrooms = Set.copyOf(classroomRepository.findAll());
                List<Instructor> instructors = instructorRepository.findAll();

                Map<Instructor, Integer> instructorLoad = instructors.stream()
                        .collect(Collectors.toMap(Function.identity(), i -> 0));
                // count how many courses a particular instructor is approved to teach
                for (CourseOffering course : courseOfferings) {
                    for (Instructor instructor : course.getApprovedInstructors()) {
                        instructorLoad.compute(instructor, (i, n) -> n + 1);
                    }
                }

                var instructorAvailabilities = new ArrayList<InstructorAvailability>();
                for (Instructor instructor : instructors) {
                    // load is the number of courses this instructor is approved to teach
                    int load = instructorLoad.get(instructor);

                    List<DayOfWeek> availableDays = new ArrayList<>(List.of(weekDays));
                    List<DayOfWeek> unavailableDays = new ArrayList<>();

                    // as load goes up, the range of the nextInt() goes down, from a maximum of 5
                    // (i.e. at most 4 unavailable days) to a minimum of 2 (at most 1).
                    int numUnavailableDays = r
                            .nextInt(weekDays.length - Math.min(load / 3, weekDays.length - 2));
                    for (int i = 0; i < numUnavailableDays; ++i) {
                        int d = r.nextInt(availableDays.size());
                        unavailableDays.add(availableDays.get(d));
                        availableDays.remove(d);
                    }

                    List<PartOfDay> availablePartsOfDay = new ArrayList<>(List.of(partsOfDay));
                    List<PartOfDay> unavailablePartsOfDay = new ArrayList<>();

                    // as load goes up, the probability of making another swathe of PartOfDays
                    // unavailable goes down (to zero for load >= 10)
                    int numUnavailablePartsOfDay = 1 + ((r.nextInt(10) >= load) ? 1 : 0);
                    for (int i = 0; i < numUnavailablePartsOfDay; ++i) {
                        int d = r.nextInt(availablePartsOfDay.size());
                        unavailablePartsOfDay.add(availablePartsOfDay.get(d));
                        availablePartsOfDay.remove(d);
                    }

                    for (int i = 0; i < availableDays.size(); ++i) {
                        for (int j = 0; j < availablePartsOfDay.size(); ++j) {
                            instructorAvailabilities.add(new InstructorAvailability(null, availableDays.get(i),
                                    availablePartsOfDay.get(j), instructor));
                        }
                    }
                }
                semesterPlanRepository.save(
                        new SemesterPlan(null, name, "Created by DebugController.populateTestSemesterPlan", semester,
                                courseOfferings, Set.copyOf(instructorAvailabilities), classrooms, Set.of(),
                                Set.of()));
            }
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/clear-data")
    public ResponseEntity<Void> clearData() {
        try {
            scheduleRepository.deleteAll();
            semesterPlanRepository.deleteAll();
            courseOfferingRepository.deleteAll();
            classroomRepository.deleteAll();
            instructorRepository.deleteAll();
            blockRequirementSplitRepository.deleteAll();
            return new ResponseEntity<Void>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
