package com.group2.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "class_schedule_assignment")
public class ClassScheduleAssignment {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    @ManyToOne
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private ClassSchedule classSchedule;

    // TODO Per the spec we need to subdivide "partOfDay" in some cases
    // i.e. some classes are run as 2 split halves
    @Getter
    @Setter
    private PartOfDay partOfDay;

    @Getter
    @Setter
    @ManyToOne
    private Instructor instructor;

}
