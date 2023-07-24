package com.group2.server.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomDto implements EntityDto {
    private Integer id;
    private String roomNumber;
    private String roomType;
}
