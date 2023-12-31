package com.group2.server.model;

import java.util.*;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@jakarta.persistence.Entity(name = "block_requirement_split")
public class BlockRequirementSplit implements Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @NonNull
    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlockRequirement> blocks;
}
