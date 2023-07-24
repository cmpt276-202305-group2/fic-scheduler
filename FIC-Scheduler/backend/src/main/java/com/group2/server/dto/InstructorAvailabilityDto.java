package com.group2.server.dto;

import com.group2.server.model.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorAvailabilityDto implements EntityDto {
    private Integer id;
    private DayOfWeek dayOfWeek;
    private PartOfDay partOfDay;
    private EntityDto instructor;
}
