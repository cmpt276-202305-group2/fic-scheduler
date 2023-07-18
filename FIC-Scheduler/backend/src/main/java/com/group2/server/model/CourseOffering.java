package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne
    private SemesterPlan semesterPlan;

    @ManyToMany
    @JoinTable(name = "facilities_course_offerings", joinColumns = @JoinColumn(name = "course_offering_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "facilities_id", referencedColumnName = "id"))
    private Set<Facilities> facilitiesRequired;

    @ManyToOne
    private Accreditation accreditationRequired;

    @ManyToOne
    private BlockType blockType;

    @ElementCollection
    private Set<String> conflictCourseNumbers;
}
