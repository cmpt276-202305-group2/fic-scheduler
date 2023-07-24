package com.group2.server.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.group2.server.model.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonDeserialize
public class InstructorAvailabilityDto implements EntityDto {
    private Integer id;
    private DayOfWeek dayOfWeek;
    private PartOfDay partOfDay;
    private EntityDto instructor;
}
