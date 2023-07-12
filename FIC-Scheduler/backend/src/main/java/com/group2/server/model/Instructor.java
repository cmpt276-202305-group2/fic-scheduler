package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Instructor")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name="Instructor_Accreditation",
        joinColumns=
            @JoinColumn(name="Instructor_ID", referencedColumnName="ID"),
        inverseJoinColumns=
            @JoinColumn(name="Accreditation_ID", referencedColumnName="ID")
        )
    private Set<Accreditation> accreditation;
}
