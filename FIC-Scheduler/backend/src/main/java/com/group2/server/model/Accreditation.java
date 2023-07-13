package com.group2.server.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Accreditaion")
public class Accreditation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  

    @Getter
    @Setter
    private String name;
    
}
