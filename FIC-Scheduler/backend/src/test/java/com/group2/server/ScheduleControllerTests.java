package com.group2.server;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.List;

import com.group2.server.model.ClassSchedule;
import com.group2.server.model.ClassScheduleAssignment;
import com.group2.server.model.Classroom;
import com.group2.server.model.Instructor;
import com.group2.server.model.InstructorAvailability;
import com.group2.server.model.PartOfDay;
import com.group2.server.model.SemesterPlan;
import com.group2.server.repository.ClassScheduleRepository;
import com.group2.server.repository.SemesterPlanRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClassScheduleRepository classScheduleRepository;

    @MockBean
    private SemesterPlanRepository semesterPlanRepository;

    // Mock some data to use in the tests
    private ClassSchedule mockSchedule;
    private SemesterPlan mockPlan;

    // ... mock other required data

    @Test
    public void testGetLatestSchedule() throws Exception {
        // Mock the data returned by the repository
        when(classScheduleRepository.findAll()).thenReturn(List.of(mockSchedule));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/schedules/latest"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockSchedule.getSemester()))
                .andDo(print());
    }
}

//     @Test
//     public void testGetScheduleById() throws Exception {
//         // Mock the data returned by the repository
//         int scheduleId = 1;
//         when(classScheduleRepository.findById(scheduleId)).thenReturn(Optional.ofNullable(mockSchedule));

//         // Perform the request and verify the response
//         mockMvc.perform(MockMvcRequestBuilders.get("/schedules/{id}", scheduleId))
//                 .andExpect(MockMvcResultMatchers.status().isOk())
//                 .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockSchedule.getSemester()))
//                 // ... verify other properties of the response
//                 .andDo(print());
//     }

//     @Test
//     public void testGetSchedulesByQuery() throws Exception {
//         // Mock the data returned by the repository
//         when(classScheduleRepository.findBySemester(anyString())).thenReturn(List.of(mockSchedule));

//         // Perform the request and verify the response
//         mockMvc.perform(MockMvcRequestBuilders.get("/schedules").param("semester", "Fall 2023"))
//                 .andExpect(MockMvcResultMatchers.status().isOk())
//                 .andExpect(MockMvcResultMatchers.jsonPath("$[0].semester").value(mockSchedule.getSemester()))
//                 // ... verify other properties of the response
//                 .andDo(print());
//     }

//     @Test
//     public void testGenerateSchedule() throws Exception {
//         // Mock the data and repository calls to simulate generating a schedule
//         // ... mock the required data for the test
//         // ... mock the required repository calls

//         // Perform the request and verify the response
//         mockMvc.perform(MockMvcRequestBuilders.post("/generate")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content("{\"semesterPlanId\": 1}")) // Assuming planId 1 exists
//                 .andExpect(MockMvcResultMatchers.status().isOk())
//                 .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockPlan.getSemester()))
//                 // ... verify other properties of the response
//                 .andDo(print());
//     }
// }