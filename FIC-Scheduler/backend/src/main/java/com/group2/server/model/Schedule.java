package com.group2.server.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "schedule")
public class Schedule {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @Column(nullable = false)
    @NonNull
    private String notes;

    @Column(nullable = false)
    @NonNull
    private String semester;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ScheduleAssignment> assignments;

    // TODO remember any interesting settings here for tracking purposes
    // The SemesterPlan used to generate this ClassSchedule might be edited after
    // generation is complete, so if we want a record of what settings resulted in
    // this schedule we need to remember it some other way than through the DB data.
    // I recommend a log text blob here, which might include log data from the
    // Google Cloud OR whatever

}
