package com.group2.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "instructor_availability")
public class InstructorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @ManyToOne
    private Instructor instructor;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private PartOfDay partOfDay;

}
