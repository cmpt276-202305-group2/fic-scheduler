package com.group2.server;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.SemesterPlanController;
import com.group2.server.dto.SemesterPlanDto;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SemesterPlanControllerTest {

    @InjectMocks
    private SemesterPlanController semesterPlanController;

    @Mock
    private SemesterPlanRepository semesterPlanRepository;

    @Mock
    private CourseOfferingRepository courseOfferingRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private ClassroomRepository classroomRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(semesterPlanController).build();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private SemesterPlan createMockSemesterPlan(Integer id, String name, String notes, String semester) {
        Set<CourseOffering> coursesOffered = new HashSet<>();
        Set<InstructorAvailability> instructorsAvailable = new HashSet<>();
        Set<Classroom> classroomsAvailable = new HashSet<>();
        return new SemesterPlan(id, name, notes, semester, coursesOffered, instructorsAvailable, classroomsAvailable);
    }

    @Test
    public void testReadListByQuery() throws Exception {
        // Mock the data returned by the repository
        List<SemesterPlan> mockSemesterPlans = new ArrayList<>();
        mockSemesterPlans.add(createMockSemesterPlan(1, "Plan 1", "Notes for Plan 1", "2023 Spring"));
        mockSemesterPlans.add(createMockSemesterPlan(2, "Plan 2", "Notes for Plan 2", "2023 Fall"));
        when(semesterPlanRepository.findAll()).thenReturn(mockSemesterPlans);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/semester-plans"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plan 1"))
                .andExpect(jsonPath("$[0].notes").value("Notes for Plan 1"))
                .andExpect(jsonPath("$[0].semester").value("2023 Spring"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Plan 2"))
                .andExpect(jsonPath("$[1].notes").value("Notes for Plan 2"))
                .andExpect(jsonPath("$[1].semester").value("2023 Fall"));

        // Verify that semesterPlanRepository.findAll() was called once
        verify(semesterPlanRepository, times(1)).findAll();
        verifyNoMoreInteractions(semesterPlanRepository);
    }
}
