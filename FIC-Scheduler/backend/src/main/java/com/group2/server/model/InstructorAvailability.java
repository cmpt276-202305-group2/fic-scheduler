package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "instructor_availability")
public class InstructorAvailability {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private PartOfDay partOfDay;

    @ManyToOne(optional = false)
    @NonNull
    private Instructor instructor;
}
