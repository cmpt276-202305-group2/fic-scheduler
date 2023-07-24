package com.group2.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "schedule_assignment")
public class ScheduleAssignment {
    @JsonIgnore
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private CourseOffering course;

    // TODO Per the spec we need to subdivide "partOfDay" in some cases
    // i.e. some classes are run as 2 split halves
    @Column(nullable = false)
    @NonNull
    private PartOfDay partOfDay;

    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(nullable = false)
    @NonNull
    private Instructor instructor;

}
