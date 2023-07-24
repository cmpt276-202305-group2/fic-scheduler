package com.group2.server.dto;

import lombok.*;

import java.util.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class CourseOfferingDto implements EntityDto {
    private Integer id;
    private String courseNumber;
    // TODO blocktypes
    // private List<String> allowedRoomTypeNames;
    private List<EntityDto> approvedInstructors;
}
