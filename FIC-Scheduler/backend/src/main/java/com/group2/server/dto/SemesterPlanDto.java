package com.group2.server.dto;

import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class SemesterPlanDto {
    private Integer id;
    private String semester;
    private List<EntityDto> coursesOffered;
    private List<InstructorAvailabilityDto> instructorsAvailable;
    private List<EntityDto> classroomsAvailable;
}
