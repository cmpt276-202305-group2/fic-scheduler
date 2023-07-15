package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "course_offering")
public class CourseOffering {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    private String courseNumber;

    @Getter
    @Setter
    @ManyToOne
    private SemesterPlan semesterPlan;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "facilities_course_offerings", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "facilities_id", referencedColumnName = "id"))
    private Set<Facilities> facilitiesRequired;

    @Getter
    @Setter
    @ManyToOne
    private Accreditation accreditationRequired;

    // TODO time requirements for this class
    // i.e. is it a 3 hour block, 2x 1.5 hour blocks, some other combination?

    // TODO instructor requirements for this class
    // some classes are shared between multiple instructors
}
