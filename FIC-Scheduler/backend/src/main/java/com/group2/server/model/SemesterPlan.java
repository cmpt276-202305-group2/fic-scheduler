package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "semester_plan")
public class SemesterPlan {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String semester;

    @OneToMany
    private Set<CourseOffering> coursesOffered;

    @OneToMany
    private Set<InstructorAvailability> instructorsAvailable;

    @ManyToMany
    @JoinTable(name = "classroom_semester_plans", joinColumns = @JoinColumn(name = "semester_plan_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "classroom_id", referencedColumnName = "id"))
    private Set<Classroom> classroomsAvailable;

}
