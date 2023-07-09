package com.group2.server.Model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Course")
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @Getter
    @Setter
    private String courseNumber;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name="Facilities_Courses",
        joinColumns=
            @JoinColumn(name="Course_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="Facilities_ID", referencedColumnName="ID")
        )
    private Set<Facilities> facilitiesRequired;

    @Getter
    @Setter
    @ManyToOne
    //@JoinColumn(name="Accreditaion_ID", nullable=false)
    private Accreditation accreditationRequired;
    
}
