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

import java.util.*;
import java.util.stream.Stream;

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

    private static final String roomTypeSmall = "Small";
    private static final String roomTypeLarge = "Large";
    private static final String roomTypeScienceLab = "Science Lab";
    private static final String roomTypeComputerLab = "Computer Lab";
    // private static final String[] roomTypes = { roomTypeSmall, roomTypeLarge,
    // roomTypeScienceLab, roomTypeComputerLab };
    private static final String[] firstNames = { "Olivia", "Sophia", "Amelia", "Emma", "Ava", "Charlotte", "Lily",
            "Hannah", "Nora", "Isabella", "Noah", "Liam", "Jackson", "Oliver", "Leo", "Lucas", "Luca", "Jack", "James",
            "Benjamin" };
    private static final String[] lastNames = { "Smith", "Brown", "Tremblay", "Martin", "Roy", "Gagnon", "Lee",
            "Wilson", "Johnson", "MacDonald", "Taylor", "Campbell", "Anderson", "Jones", "Leblanc", "Cote", "Williams",
            "Miller", "Thompson", "Gauthier" };
    private static final String[] fullNames = new String[60];

    private static final String halfPeriodSmallRoom = "Half period/small room";
    private static final String fullPeriodSmallRoom = "Full period/small room";
    private static final String fullPeriodLargeRoom = "Full period/large room";
    private static final String fullPeriodSmallRoomAndHalfPeriodComputerLab = "Full period/small room + half period/computer lab";
    private static final String fullPeriodSmallRoomAndFullPeriodScienceLab = "Full period/small room + full period/science lab";
    private static final Map<String, List<BlockRequirement>> blockRequirements = Map.ofEntries(
            Map.entry(halfPeriodSmallRoom,
                    List.of(new BlockRequirement(null, Set.of(roomTypeSmall), Duration.HALF))),
            Map.entry(fullPeriodSmallRoom,
                    List.of(new BlockRequirement(null, Set.of(roomTypeSmall, roomTypeLarge), Duration.FULL))),
            Map.entry(fullPeriodLargeRoom,
                    List.of(new BlockRequirement(null, Set.of(roomTypeLarge), Duration.FULL))),
            Map.entry(fullPeriodSmallRoomAndHalfPeriodComputerLab,
                    List.of(new BlockRequirement(null, Set.of(roomTypeSmall, roomTypeLarge), Duration.FULL),
                            new BlockRequirement(null, Set.of(roomTypeComputerLab), Duration.HALF))),
            Map.entry(fullPeriodSmallRoomAndFullPeriodScienceLab,
                    List.of(new BlockRequirement(null, Set.of(roomTypeSmall, roomTypeLarge), Duration.FULL),
                            new BlockRequirement(null, Set.of(roomTypeScienceLab), Duration.FULL))));
    private static final Map<String, String> classrooms = Map.ofEntries(
            Map.entry("DISC1 2400 (L)", roomTypeLarge),
            Map.entry("DISC1 2420", roomTypeSmall),
            Map.entry("DISC1 2440", roomTypeSmall),
            Map.entry("DISC1 2460 (C)", roomTypeComputerLab),
            Map.entry("DISC1 2480 (C)", roomTypeComputerLab),
            Map.entry("DISC1 2500 (L)", roomTypeLarge),
            Map.entry("DISC1 2520", roomTypeSmall),
            Map.entry("DISC1 2540", roomTypeSmall),
            Map.entry("DISC1 2560", roomTypeSmall),
            Map.entry("DISC1 2580 (S)", roomTypeScienceLab),
            Map.entry("DISC1 3420", roomTypeSmall),
            Map.entry("DISC1 3440", roomTypeSmall),
            Map.entry("DISC1 3460", roomTypeSmall),
            Map.entry("DISC1 3480", roomTypeSmall),
            Map.entry("DISC1 3520", roomTypeSmall),
            Map.entry("DISC1 3540", roomTypeSmall),
            Map.entry("DISC1 3560", roomTypeSmall));
    private static final String[] subjects = { "CMPT", "PHYS", "ENGL", "BUS", "POL", "ECON", "ILS", "ALC" };

    static {
        for (int i = 0; i < fullNames.length; ++i) {
            var r = new Random(13);
            for (i = 0; i < fullNames.length; ++i) {
                fullNames[i] = firstNames[r.nextInt(firstNames.length)] + " " + lastNames[r.nextInt(lastNames.length)];
            }
        }
    }

    @PostMapping("/populate-test-blocks")
    public ResponseEntity<Void> populateTestBlocks() {
        try {
            for (var e : blockRequirements.entrySet()) {
                if (blockRequirementSplitRepository.findByName(e.getKey()).size() == 0) {
                    blockRequirementSplitRepository.save(new BlockRequirementSplit(null, e.getKey(), e.getValue()));
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
            for (var e : classrooms.entrySet()) {
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
        try {
            var r = new Random(40);
            var instructors = instructorRepository.findAll().toArray(new Instructor[0]);
            var ilsAllowedBlockSplits = blockRequirementSplitRepository.findByName(halfPeriodSmallRoom);
            var cmptLabAllowedBlockSplits = blockRequirementSplitRepository
                    .findByName(fullPeriodSmallRoomAndHalfPeriodComputerLab);
            var physLabAllowedBlockSplits = blockRequirementSplitRepository
                    .findByName(fullPeriodSmallRoomAndFullPeriodScienceLab);
            var genericAllowedBlockSplits = Set
                    .copyOf(Stream.concat(blockRequirementSplitRepository.findByName(fullPeriodSmallRoom).stream(),
                            blockRequirementSplitRepository.findByName(fullPeriodLargeRoom).stream()).toList());

            for (var subject : subjects) {
                // Shuffle instructor priorities for the subject
                for (int i = 0; (i + 2) < instructors.length; ++i) {
                    var j = (i + 1) + r.nextInt(instructors.length - (i + 1));
                    var t = instructors[i];
                    instructors[i] = instructors[j];
                    instructors[j] = t;
                }

                var numCourses = subject.equals("ILS") || subject.equals("ALC") ? 1 : 6;
                var numSections = 4;
                var numberPart = subject.equals("ILS") ? 101 : (subject.equals("ALC") ? 99 : 100);

                for (int i = 0; i < numCourses; ++i) {
                    int minApprovedInstructors = Math.min((numSections + 1) / 2, instructors.length);
                    int maxApprovedInstructors = Math.min(minApprovedInstructors + numSections, instructors.length);
                    var approvedInstructors = new ArrayList<Instructor>(maxApprovedInstructors);
                    for (int j = 0; (j < instructors.length)
                            && (approvedInstructors.size() >= maxApprovedInstructors); ++j) {
                        if (r.nextBoolean()) {
                            approvedInstructors.add(instructors[j]);
                        }
                    }
                    if (approvedInstructors.size() < minApprovedInstructors) {
                        approvedInstructors.clear();
                        for (int j = 0; j < minApprovedInstructors; ++j) {
                            approvedInstructors.add(instructors[j]);
                        }
                    }

                    Set<BlockRequirementSplit> allowedBlockSplits = null;
                    if (subject.equals("ILS")) {
                        allowedBlockSplits = ilsAllowedBlockSplits;
                        // ALC099 halfPeriodSmallRoom
                    } else if (subject.equals("CMPT") && ((i == 1) || (i == 2))) {
                        allowedBlockSplits = cmptLabAllowedBlockSplits;
                    } else if (subject.equals("PHYS") && ((i == 1) || (i == 2))) {
                        allowedBlockSplits = physLabAllowedBlockSplits;
                    } else {
                        allowedBlockSplits = genericAllowedBlockSplits;
                    }
                    for (int section = 0; section < numSections; ++section) {
                        courseOfferingRepository.save(new CourseOffering(null,
                                String.format("%s%03d/O%d", subject, numberPart, section),
                                String.format("%s%03d", subject, numberPart),
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
            for (var name : fullNames) {
                if (!usedNames.contains(name)) {
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
        try {
            semesterPlanRepository.save(new SemesterPlan(null, null, null, null, null, null, null, null, null));
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
