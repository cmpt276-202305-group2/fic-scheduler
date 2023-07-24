package com.group2.server.dto;

import com.group2.server.model.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class BlockAssignmentDto {
    private EntityDto instructor;
    // probably include the whole ClassroomDto?
    private EntityDto classroom;
    private DayOfWeek day;
    private PartOfDay time;
}
