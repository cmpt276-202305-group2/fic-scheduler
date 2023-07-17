package com.group2.server.model;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

@Data
@AllArgsConstructor
@Entity(name = "instructor")
public class Instructor {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToMany
    @JoinTable(name = "instructor_accreditation", joinColumns = @JoinColumn(name = "instructor_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "accreditation_id", referencedColumnName = "id"))
    private Set<Accreditation> accreditation;

}
