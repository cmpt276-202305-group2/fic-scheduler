package com.group2.server.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "class_schedule")
public class ClassSchedule {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String semester;

    @OneToMany(mappedBy = "classSchedule")
    private Set<ClassScheduleAssignment> classScheduleAssignments;

    // TODO remember any interesting settings here for tracking purposes
    // The SemesterPlan used to generate this ClassSchedule might be edited after
    // generation is complete, so if we want a record of what settings resulted in
    // this schedule we need to remember it some other way than through the DB data.
    // I recommend a log text blob here, which might include log data from the
    // Google Cloud OR whatever

}
