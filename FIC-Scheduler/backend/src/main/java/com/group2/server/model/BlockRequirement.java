package com.group2.server.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "block_requirement")
public class BlockRequirement implements Entity {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    private Set<String> allowedRoomTypes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private Duration duration;

}
