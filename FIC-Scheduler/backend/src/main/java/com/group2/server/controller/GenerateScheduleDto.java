package com.group2.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateScheduleDto {
    private Integer semesterPlanId;

    // TODO any other ephemeral parameters here
    // e.g. if the user requested relaxation of any constraints, or requested extra
    // error logging, or etc.
}
