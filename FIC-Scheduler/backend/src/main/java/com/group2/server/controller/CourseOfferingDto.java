package com.group2.server.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOfferingDto {
    private Integer id;
    private String courseNumber;
    private Integer semesterPlanId;
    private Set<String> facilitiesRequiredNames;
    private String accreditationRequiredName;
    private String blockTypeName;
    private Set<String> conflictCourseNumbers;
}
