package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class CourseCorequisiteDto {
    private EntityDto courseA;
    private EntityDto courseB;
}
