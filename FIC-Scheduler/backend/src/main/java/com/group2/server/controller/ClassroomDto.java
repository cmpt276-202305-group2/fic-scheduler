package com.group2.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassroomDto {
    private Integer id;
    private String roomNumber;
    private Set<Integer> facilitiesAvailableIds;
}
