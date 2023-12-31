package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class GenerateScheduleDto {
    private EntityDto semesterPlan;

    // TODO any other ephemeral parameters here
    // e.g. if the user requested relaxation of any constraints, or requested extra
    // error logging, or etc.
}
