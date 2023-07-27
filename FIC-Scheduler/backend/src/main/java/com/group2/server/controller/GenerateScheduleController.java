package com.group2.server.controller;

import java.util.*;
import java.util.regex.*;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;
import com.group2.server.dto.GenerateScheduleDto;
import com.group2.server.model.*;
import com.group2.server.repository.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GenerateScheduleController {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SemesterPlanRepository semesterPlanRepository;

    private final Pattern baseNamePattern = Pattern.compile("^(.*?)(?>\\s*-\\s*[0-9]+)?$");

    @PostMapping("/generate-schedule")
    public ResponseEntity<Void> generateSchedule(@RequestBody GenerateScheduleDto generateScheduleDto) {
        try {
            Schedule sched = new Schedule();
            Integer planId = generateScheduleDto.getSemesterPlan().getId();
            SemesterPlan plan = planId != null ? semesterPlanRepository.findById(planId).orElse(null) : null;

            if (plan == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            var scheduleNames = Set.copyOf(scheduleRepository.findAll().stream().map(s -> s.getName()).toList());
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

            sched.setName(name);
            sched.setNotes(String.format("Generated schedule from %s (%s):\n%s", plan.getName(), plan.getSemester(),
                    plan.getNotes()));
            sched.setSemester(plan.getSemester());

            // TODO test code just generates a random schedule -- make it real
            HashSet<ScheduleAssignment> assignments = new HashSet<>();
            var r = new Random();
            var daysOfWeek = new DayOfWeek[] { DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY };
            var partsOfDay = new PartOfDay[] { PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING };
            var classrooms = plan.getClassroomsAvailable().toArray(new Classroom[0]);
            var instructor_availabilities = plan.getInstructorsAvailable().toArray(new InstructorAvailability[0]);

            for (var course : plan.getCoursesOffered()) {
                var dayOfWeek = daysOfWeek[r.nextInt(partsOfDay.length)];
                var partOfDay = partsOfDay[r.nextInt(partsOfDay.length)];
                var classroom = classrooms[r.nextInt(classrooms.length)];
                var instructor = instructor_availabilities[r.nextInt(instructor_availabilities.length)].getInstructor();
                assignments.add(new ScheduleAssignment(null, dayOfWeek, partOfDay, classroom, course, instructor));
            }
            sched.setAssignments(assignments);
            scheduleRepository.save(sched);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    int numNurses = 4;
    int numDays = 3;
    int numShifts = 3;

    int[] allNurses = IntStream.range(0, numNurses).toArray();
    int[] allDays = IntStream.range(0, numDays).toArray();
    int[] allShifts = IntStream.range(0, numShifts).toArray();

    public void orSample() {
        CpModel model = new CpModel();

        Literal[][][] shifts = new Literal[numNurses][numDays][numShifts];
        for (int n : allNurses) {
            for (int d : allDays) {
                for (int s : allShifts) {
                    shifts[n][d][s] = model.newBoolVar("shifts_n" + n + "d" + d + "s" + s);
                }
            }
        }

        for (int d : allDays) {
            for (int s : allShifts) {
                List<Literal> nurses = new ArrayList<>();
                for (int n : allNurses) {
                    nurses.add(shifts[n][d][s]);
                }
                model.addExactlyOne(nurses);
            }
        }

        for (int n : allNurses) {
            for (int d : allDays) {
                List<Literal> work = new ArrayList<>();
                for (int s : allShifts) {
                    work.add(shifts[n][d][s]);
                }
                model.addAtMostOne(work);
            }
        }

        // Try to distribute the shifts evenly, so that each nurse works
        // minShiftsPerNurse shifts. If this is not possible, because the total
        // number of shifts is not divisible by the number of nurses, some nurses will
        // be assigned one more shift.
        int minShiftsPerNurse = (numShifts * numDays) / numNurses;
        int maxShiftsPerNurse;
        if ((numShifts * numDays) % numNurses == 0) {
            maxShiftsPerNurse = minShiftsPerNurse;
        } else {
            maxShiftsPerNurse = minShiftsPerNurse + 1;
        }
        for (int n : allNurses) {
            LinearExprBuilder shiftsWorked = LinearExpr.newBuilder();
            for (int d : allDays) {
                for (int s : allShifts) {
                    shiftsWorked.add(shifts[n][d][s]);
                }
            }
            model.addLinearConstraint(shiftsWorked, minShiftsPerNurse, maxShiftsPerNurse);
        }

        CpSolver solver = new CpSolver();
        solver.getParameters().setLinearizationLevel(0);
        // Tell the solver to enumerate all solutions.
        solver.getParameters().setEnumerateAllSolutions(true);

        final int solutionLimit = 5;
        class VarArraySolutionPrinterWithLimit extends CpSolverSolutionCallback {
            public VarArraySolutionPrinterWithLimit(
                    int[] allNurses, int[] allDays, int[] allShifts, Literal[][][] shifts, int limit) {
                solutionCount = 0;
                this.allNurses = allNurses;
                this.allDays = allDays;
                this.allShifts = allShifts;
                this.shifts = shifts;
                solutionLimit = limit;
            }

            @Override
            public void onSolutionCallback() {
                System.out.printf("Solution #%d:%n", solutionCount);
                for (int d : allDays) {
                    System.out.printf("Day %d%n", d);
                    for (int n : allNurses) {
                        boolean isWorking = false;
                        for (int s : allShifts) {
                            if (booleanValue(shifts[n][d][s])) {
                                isWorking = true;
                                System.out.printf("  Nurse %d work shift %d%n", n, s);
                            }
                        }
                        if (!isWorking) {
                            System.out.printf("  Nurse %d does not work%n", n);
                        }
                    }
                }
                solutionCount++;
                if (solutionCount >= solutionLimit) {
                    System.out.printf("Stop search after %d solutions%n", solutionLimit);
                    stopSearch();
                }
            }

            public int getSolutionCount() {
                return solutionCount;
            }

            private int solutionCount;
            private final int[] allNurses;
            private final int[] allDays;
            private final int[] allShifts;
            private final Literal[][][] shifts;
            private final int solutionLimit;
        }

        VarArraySolutionPrinterWithLimit cb = new VarArraySolutionPrinterWithLimit(allNurses, allDays, allShifts,
                shifts, solutionLimit);

        CpSolverStatus status = solver.solve(model, cb);
        System.out.println("Status: " + status);
        System.out.println(cb.getSolutionCount() + " solutions found.");
    }

}
