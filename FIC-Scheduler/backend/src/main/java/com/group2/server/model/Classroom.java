package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "classroom")
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Getter
    @Setter
    private String roomNumber;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name="Classroom_Facilities",
        joinColumns=
            @JoinColumn(name="Classroom_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="Facilities_ID", referencedColumnName="ID")
        )
    private Set<Facilities> facilitiesAvailable;
    
}
