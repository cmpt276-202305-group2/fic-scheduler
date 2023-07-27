package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "instructor_scheduling_request")
public class InstructorSchedulingRequest {
    @Setter(AccessLevel.PROTECTED)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @NonNull
    private Instructor instructor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NonNull
    private SchedulingRequest schedulingRequest;

}
