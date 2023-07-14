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
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Getter
    @Setter
    private String roomNumber;

    @Getter
    @Setter
    @ManyToMany
    @JoinTable(name = "classroom_facilities", joinColumns = @JoinColumn(name = "classroom_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "facilities_id", referencedColumnName = "id"))
    private Set<Facilities> facilitiesAvailable;

}
