package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.group2.server.model.*;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class BlockRequirementDto {
    private List<String> allowedRoomTypes;
    private Duration duration;
}
