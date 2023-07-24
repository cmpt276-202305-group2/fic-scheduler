package com.group2.server.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorDto implements EntityDto {
    private Integer id;
    private String name;
}
