package com.group2.server.controller;

import lombok.Data;

@Data
public class GenerateScheduleDto {
    private Integer semesterPlanId;

    // TODO any other ephemeral parameters here
    // e.g. if the user requested relaxation of any constraints, or requested extra
    // error logging, or etc.
}
