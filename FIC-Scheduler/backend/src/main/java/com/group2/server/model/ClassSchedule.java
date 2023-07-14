package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "class_schedule")
public class ClassSchedule {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    private String semester;

    @Getter
    @Setter
    @OneToMany(mappedBy = "classSchedule")
    private Set<ClassScheduleAssignment> classScheduleAssignments;

    // TODO remember any interesting settings here for tracking purposes
    // The SemesterPlan used to generate this ClassSchedule might be edited after
    // generation is complete, so if we want a record of what settings resulted in
    // this schedule we need to remember it some other way than through the DB data.
    // I recommend a log text blob here, which might include log data from the
    // Google Cloud OR whatever

}
