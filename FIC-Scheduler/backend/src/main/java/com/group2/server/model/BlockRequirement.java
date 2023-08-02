package com.group2.server.model;

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
    private String roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private Duration duration;

}
