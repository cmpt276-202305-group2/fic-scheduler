package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class InstructorDto implements EntityDto {
    private Integer id;
    private String name;
    private String notes;
}
