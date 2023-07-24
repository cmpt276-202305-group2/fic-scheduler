package com.group2.server.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "block_requirement_division")
public class BlockRequirementDivision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @OneToMany
    private Set<BlockRequirement> blocks;
}
