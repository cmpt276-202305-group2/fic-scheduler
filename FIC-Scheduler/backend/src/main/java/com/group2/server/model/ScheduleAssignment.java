package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "schedule_assignment")
public class ScheduleAssignment {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NonNull
    private PartOfDay partOfDay;

    @ManyToOne(optional = false)
    @NonNull
    private Classroom classroom;

    @ManyToOne(optional = false)
    @NonNull
    private CourseOffering course;

    @ManyToOne(optional = false)
    @NonNull
    private Instructor instructor;

}
