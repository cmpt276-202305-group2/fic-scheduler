package com.group2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.SemesterPlanController;
import com.group2.server.dto.SemesterPlanDto;
import com.group2.server.model.*;
import com.group2.server.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        Set<CourseCorequisite> courseCorequisites = new HashSet<>();
        Set<InstructorSchedulingRequest> instructorSchedulingRequests = new HashSet<>();
        return new SemesterPlan(id, name, notes, semester, coursesOffered, instructorsAvailable, classroomsAvailable,
                courseCorequisites, instructorSchedulingRequests);
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
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plan 1"))
                .andExpect(jsonPath("$[0].notes").value("Notes for Plan 1"))
                .andExpect(jsonPath("$[0].semester").value("2023 Spring"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Plan 2"))
                .andExpect(jsonPath("$[1].notes").value("Notes for Plan 2"))
                .andExpect(jsonPath("$[1].semester").value("2023 Fall"));

        verify(semesterPlanRepository, times(1)).findAll();
        verifyNoMoreInteractions(semesterPlanRepository);
    }

    @Test
    public void testReadListByQueryExceptionCase() throws Exception {
        // Mock the repository to throw an exception when calling findAll()
        when(semesterPlanRepository.findAll()).thenThrow(new RuntimeException("Error occurred while fetching data"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/semester-plans"))
                .andExpect(status().isBadRequest());

        // Verify that semesterPlanRepository.findAll() was called once
        verify(semesterPlanRepository, times(1)).findAll();
    }

    @Test
    public void testReadOneById() throws Exception {
        // Mock the data returned by the repository
        SemesterPlan mockSemesterPlan = createMockSemesterPlan(1, "Plan 1", "Notes for Plan 1", "2023 Spring");
        when(semesterPlanRepository.findById(1)).thenReturn(Optional.of(mockSemesterPlan));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/semester-plans/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Plan 1"))
                .andExpect(jsonPath("$.notes").value("Notes for Plan 1"))
                .andExpect(jsonPath("$.semester").value("2023 Spring"));

        verify(semesterPlanRepository, times(1)).findById(1);
        verifyNoMoreInteractions(semesterPlanRepository);
    }

    @Test
    public void testReadOneByIdExceptionCase() throws Exception {
        int semesterPlanId = 1;

        // Mock the repository to return an empty optional, simulating the id not found
        when(semesterPlanRepository.findById(semesterPlanId)).thenReturn(Optional.empty());

        // Perform the request and verify the response
        mockMvc.perform(get("/api/semester-plans/{id}", semesterPlanId))
                .andExpect(status().isBadRequest());

        // Verify that semesterPlanRepository.findById() was called once with the
        // correct ID
        verify(semesterPlanRepository, times(1)).findById(semesterPlanId);
    }

    @Test
    public void testCreateOrUpdateList() throws Exception {
        // Mock the data to be sent in the request
        List<SemesterPlanDto> semesterPlanDtoList = new ArrayList<>();
        semesterPlanDtoList.add(new SemesterPlanDto(null, "Plan 1", "Notes for Plan 1", "2023 Spring",
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
        semesterPlanDtoList.add(new SemesterPlanDto(null, "Plan 2", "Notes for Plan 2", "2023 Fall", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));

        SemesterPlan savedSemesterPlans_1 = createMockSemesterPlan(1, "Plan 1", "Notes for Plan 1", "2023 Spring");
        SemesterPlan savedSemesterPlans_2 = createMockSemesterPlan(2, "Plan 2", "Notes for Plan 2", "2023 Fall");

        when(semesterPlanRepository.save(any(SemesterPlan.class)))
                .thenReturn(savedSemesterPlans_1)
                .thenReturn(savedSemesterPlans_2);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/semester-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(semesterPlanDtoList)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Plan 1"))
                .andExpect(jsonPath("$[0].notes").value("Notes for Plan 1"))
                .andExpect(jsonPath("$[0].semester").value("2023 Spring"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Plan 2"))
                .andExpect(jsonPath("$[1].notes").value("Notes for Plan 2"))
                .andExpect(jsonPath("$[1].semester").value("2023 Fall"));

        verify(semesterPlanRepository, times(2)).save(any(SemesterPlan.class));
        verifyNoMoreInteractions(semesterPlanRepository);
    }

    @Test
    public void testCreateOrUpdateListExceptionCase() throws Exception {
        // Mock the repository to throw an exception when saving
        when(semesterPlanRepository.save(any(SemesterPlan.class))).thenThrow(new RuntimeException());
    
        // Mock the data in the request body, where the first SemesterPlanDto is valid,
        // and the second one will cause an exception when trying to save it
        List<SemesterPlanDto> semesterPlanDtoList = new ArrayList<>();
        semesterPlanDtoList.add(new SemesterPlanDto(null, "Plan 1", "Notes for Plan 1", "Fall 2023", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList(), new ArrayList()));
        semesterPlanDtoList.add(new SemesterPlanDto(null, "Invalid Plan", "Invalid Notes", "Invalid Semester", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList(), new ArrayList()));
    
        // Perform the request and verify the response
        mockMvc.perform(post("/api/semester-plans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(semesterPlanDtoList)))
                .andExpect(status().isBadRequest());
    
        // Verify that semesterPlanRepository.save() was called once with the correct entity
        verify(semesterPlanRepository, times(1)).save(any(SemesterPlan.class));
    }

    @Test
    public void testUpdateOneById() throws Exception {
        // Mock the data to be sent in the request
        int semesterPlanId = 1;
        SemesterPlanDto semesterPlanDto = new SemesterPlanDto(semesterPlanId, "Updated Plan", "Updated Notes",
                "2023 Fall", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                new ArrayList<>());

        // Mock the data returned by the repository after updating
        SemesterPlan updatedSemesterPlan = createMockSemesterPlan(semesterPlanId, "Updated Plan", "Updated Notes",
                "2023 Fall");
        when(semesterPlanRepository.findById(semesterPlanId)).thenReturn(
                Optional.of(createMockSemesterPlan(semesterPlanId, "Plan 1", "Notes for Plan 1", "2023 Spring")));
        when(semesterPlanRepository.save(any())).thenReturn(updatedSemesterPlan);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/semester-plans/{id}", semesterPlanId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(semesterPlanDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(semesterPlanId))
                .andExpect(jsonPath("$.name").value("Updated Plan"))
                .andExpect(jsonPath("$.notes").value("Updated Notes"))
                .andExpect(jsonPath("$.semester").value("2023 Fall"));

        verify(semesterPlanRepository, times(1)).findById(semesterPlanId);

        ArgumentCaptor<SemesterPlan> semesterPlanCaptor = ArgumentCaptor.forClass(SemesterPlan.class);
        verify(semesterPlanRepository, times(1)).save(semesterPlanCaptor.capture());
        verifyNoMoreInteractions(semesterPlanRepository);

        // Verify the updated SemesterPlan data
        SemesterPlan capturedSemesterPlan = semesterPlanCaptor.getValue();
        assertEquals(1, capturedSemesterPlan.getId());
        assertEquals("Updated Plan", capturedSemesterPlan.getName());
        assertEquals("Updated Notes", capturedSemesterPlan.getNotes());
        assertEquals("2023 Fall", capturedSemesterPlan.getSemester());
    }

    @Test
    public void testUpdateOneByIdExceptionCase() throws Exception {
        int semesterPlanId = 1;
        SemesterPlanDto semesterPlanDto = new SemesterPlanDto(2, "Plan 1", "Notes for Plan 1", "Fall 2023", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),  new ArrayList(), new ArrayList());

        // Perform the request and verify the response
        mockMvc.perform(put("/api/semester-plans/{id}", semesterPlanId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(semesterPlanDto)))
                .andExpect(status().isBadRequest());

        // Verify that semesterPlanRepository.findById() was not called
        verify(semesterPlanRepository, times(0)).findById(semesterPlanId);
        verifyNoInteractions(semesterPlanRepository);
    }

    @Test
    public void testDeleteOneById() throws Exception {
        // Perform the request and verify the response
        mockMvc.perform(delete("/api/semester-plans/{id}", 1))
                .andExpect(status().isOk());

        verify(semesterPlanRepository, times(1)).deleteById(1);
        verifyNoMoreInteractions(semesterPlanRepository);
    }

    @Test
    public void testDeleteOneByIdExceptionCase() throws Exception {

        doThrow(IllegalArgumentException.class).when(semesterPlanRepository).deleteById(any(Integer.class));
        // Perform the request and verify the response
        mockMvc.perform(delete("/api/semester-plans/{id}", 1))
                .andExpect(status().isBadRequest());

        verify(semesterPlanRepository, times(1)).deleteById(any(Integer.class));
        verifyNoMoreInteractions(semesterPlanRepository);
    }

}
