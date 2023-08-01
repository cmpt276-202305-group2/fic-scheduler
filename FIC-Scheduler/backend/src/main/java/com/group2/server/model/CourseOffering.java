package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "course_offering")
public class CourseOffering implements Entity {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @Column(nullable = false)
    @NonNull
    private String courseNumber;

    @Column(nullable = false)
    @NonNull
    private String notes;

    @ManyToMany
    @JoinTable(name = "course_offerings_approved_instructors", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "approved_instructor_id", referencedColumnName = "id"))
    private Set<Instructor> approvedInstructors;

    @ManyToMany
    @JoinTable(name = "course_offerings_allowed_block_splits", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "block_split_id", referencedColumnName = "id"))
    private Set<BlockRequirementSplit> allowedBlockSplits;
}
