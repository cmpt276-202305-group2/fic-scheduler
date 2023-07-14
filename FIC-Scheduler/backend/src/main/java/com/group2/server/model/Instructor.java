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

@Entity(name = "instructor")
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
    @JoinTable(name = "instructor_accreditation", joinColumns = @JoinColumn(name = "instructor_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "accreditation_id", referencedColumnName = "id"))
    private Set<Accreditation> accreditation;
}
