package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.group2.server.model.*;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class BlockRequirementDto {
    private String roomType;
    private Duration duration;
}
