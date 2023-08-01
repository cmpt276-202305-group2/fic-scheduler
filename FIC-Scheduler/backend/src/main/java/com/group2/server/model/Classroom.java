package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "classroom")
public class Classroom implements Entity {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NonNull
    private String roomNumber;

    @Column(nullable = false)
    @NonNull
    private String roomType;

    @Column(nullable = false)
    @NonNull
    private String notes;

}
