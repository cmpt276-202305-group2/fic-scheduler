package com.group2.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "instructor_availability")
public class InstructorAvailability {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private SemesterPlan semesterPlan;

    @ManyToOne
    private Instructor instructor;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    private PartOfDay partOfDay;

}
