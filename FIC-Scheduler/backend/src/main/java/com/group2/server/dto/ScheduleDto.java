package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.group2.server.model.ClassScheduleAssignment;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize

public class ScheduleDto implements EntityDto {
    private Integer id;
    private String semester;
}
