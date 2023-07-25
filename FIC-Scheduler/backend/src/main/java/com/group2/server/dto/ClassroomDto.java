package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class ClassroomDto implements EntityDto {
    private Integer id;
    private String roomNumber;
    private String roomType;
    private String notes;
}
