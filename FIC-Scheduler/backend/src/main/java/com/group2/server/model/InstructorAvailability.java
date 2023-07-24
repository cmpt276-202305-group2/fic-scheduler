package com.group2.server.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "instructor_availability")
public class InstructorAvailability {
  @Setter(AccessLevel.PROTECTED)
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Enumerated(EnumType.STRING)
  private DayOfWeek dayOfWeek;

  @Enumerated(EnumType.STRING)
  private PartOfDay partOfDay;

  @ManyToOne
  private Instructor instructor;
}
