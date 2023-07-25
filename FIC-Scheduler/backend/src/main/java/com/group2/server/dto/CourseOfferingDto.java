package com.group2.server.dto;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CourseOfferingDto implements EntityDto {
    private Integer id;
    private String name;
    private String courseNumber;
    private String notes;
    private List<EntityDto> approvedInstructors;
    private List<EntityDto> blockDivisions;
}
