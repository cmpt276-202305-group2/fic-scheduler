package com.group2.server.dto;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CourseOfferingDto implements EntityDto {
    private Integer id;
    private String courseNumber;
    private List<EntityDto> approvedInstructors;
    private List<EntityDto> blockDivisions;
}
