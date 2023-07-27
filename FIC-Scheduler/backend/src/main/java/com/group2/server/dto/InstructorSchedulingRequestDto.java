package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.group2.server.model.SchedulingRequest;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class InstructorSchedulingRequestDto {
    private EntityDto instructor;
    private SchedulingRequest request;
}
