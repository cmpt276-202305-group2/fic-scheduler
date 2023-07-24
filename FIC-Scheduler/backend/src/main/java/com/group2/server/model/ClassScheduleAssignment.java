package com.group2.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "class_schedule_assignment")
public class ClassScheduleAssignment {
    @JsonIgnore
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "class_schedule_id", nullable = false)
    private Schedule classSchedule;

    private String courseNumber;

    // TODO Per the spec we need to subdivide "partOfDay" in some cases
    // i.e. some classes are run as 2 split halves
    private PartOfDay partOfDay;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private Instructor instructor;

}
