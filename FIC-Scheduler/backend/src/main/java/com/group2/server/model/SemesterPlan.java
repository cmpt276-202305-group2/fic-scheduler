package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "semesterPlan")
    private Set<CourseOffering> coursesOffered;

    @OneToMany(mappedBy = "semesterPlan")
    private Set<InstructorAvailability> instructorsAvailable;

    @ManyToMany
    @JoinTable(name = "classroom_semester_plans", joinColumns = @JoinColumn(name = "semester_plan_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "classroom_id", referencedColumnName = "id"))
    private Set<Classroom> classroomsAvailable;

}
