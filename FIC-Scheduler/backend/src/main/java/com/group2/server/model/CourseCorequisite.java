package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "course_corequisite")
public class CourseCorequisite implements Entity {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @NonNull
    private CourseOffering courseA;

    @ManyToOne(optional = false)
    @NonNull
    private CourseOffering courseB;

}
