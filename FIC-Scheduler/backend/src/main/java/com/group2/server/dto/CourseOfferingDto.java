package com.group2.server.dto;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOfferingDto implements EntityDto {
    private Integer id;
    private String courseNumber;
    // TODO blocktypes
    // private List<String> allowedRoomTypeNames;
    private List<EntityDto> approvedInstructors;
}
