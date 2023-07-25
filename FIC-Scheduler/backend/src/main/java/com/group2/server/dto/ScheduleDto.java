package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class ScheduleDto implements EntityDto {
    private Integer id;
    private String name;
    private String notes;
    private String semester;
    private List<CourseAssignmentDto> courses;
}
