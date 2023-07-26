package com.group2.server.model;

import java.util.*;

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

    @Column(nullable = false)
    @NonNull
    private String name;

    @Column(nullable = false)
    @NonNull
    private String notes;

    @Column(nullable = false)
    @NonNull
    private String semester;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseOffering> coursesOffered;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InstructorAvailability> instructorsAvailable;

    @ManyToMany()
    @JoinTable(name = "classrooms_available_semester_plans", joinColumns = @JoinColumn(name = "semester_plan_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "classroom_available_id", referencedColumnName = "id"))
    private Set<Classroom> classroomsAvailable;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseCorequisite> courseCorequisites;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InstructorSchedulingRequest> instructorSchedulingRequests;
}
