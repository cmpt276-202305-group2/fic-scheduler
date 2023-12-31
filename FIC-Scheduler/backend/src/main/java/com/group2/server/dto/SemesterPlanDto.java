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
    private String name;
    private String notes;
    private String semester;
    private List<EntityDto> coursesOffered;
    private List<InstructorAvailabilityDto> instructorsAvailable;
    private List<EntityDto> classroomsAvailable;
    private List<CourseCorequisiteDto> courseCorequisites;
    private List<InstructorSchedulingRequestDto> instructorSchedulingRequests;
}
