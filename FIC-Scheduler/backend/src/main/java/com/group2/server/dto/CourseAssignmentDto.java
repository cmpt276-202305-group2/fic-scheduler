package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class CourseAssignmentDto {
    private EntityDto course;
    private List<BlockAssignmentDto> blocks;
}
