package com.group2.server.controller;


import com.group2.server.model.DayOfWeek;
import com.group2.server.model.PartOfDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstructorAvailabilityDto {
    // private Integer id;
    private DayOfWeek dayOfWeek;
    private PartOfDay partOfDay;
    private String instructorName;
    //private Integer semesterPlanId; //uncomment when SemesterPlan is implemented
}
