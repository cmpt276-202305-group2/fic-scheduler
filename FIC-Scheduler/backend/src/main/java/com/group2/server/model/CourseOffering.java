package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "course_offering")
public class CourseOffering {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String courseNumber;

    @ManyToMany
    @JoinTable(name = "course_offerings_approved_instructors", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "approved_instructor_id", referencedColumnName = "id"))
    private Set<Instructor> approvedInstructors;

    @ManyToMany
    @JoinTable(name = "course_offerings_block_requirements", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "block_requirement_id", referencedColumnName = "id"))
    private Set<BlockRequirement> blockRequirements;
}
