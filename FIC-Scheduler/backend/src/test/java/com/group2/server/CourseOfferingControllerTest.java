package com.group2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.CourseOfferingController;
import com.group2.server.dto.CourseOfferingDto;
// import com.group2.server.model.CourseOffering;
import com.group2.server.model.*;
import com.group2.server.repository.*;
// import com.group2.server.repository.CourseOfferingRepository;
// import com.group2.server.repository.BlockRequirementSplitRepository;
// import com.group2.server.repository.InstructorRepository;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CourseOfferingControllerTest {

    @InjectMocks
    private CourseOfferingController courseOfferingController;

    @Mock
    private CourseOfferingRepository courseOfferingRepository;

    @Mock
    private BlockRequirementSplitRepository blockRequirementSplitRepository;

    @Mock
    private InstructorRepository instructorRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseOfferingController).build();
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testReadListByQuery() throws Exception {
        // Mock the data returned by the repository
        List<CourseOffering> mockCourseOfferings = new ArrayList<>();
        mockCourseOfferings.add(new CourseOffering(1, "Course 1", "COURSE101", "Notes 1",
                new HashSet<>(), new HashSet<>()));
        mockCourseOfferings.add(new CourseOffering(2, "Course 2", "COURSE202", "Notes 2",
                new HashSet<>(), new HashSet<>()));
        when(courseOfferingRepository.findAll()).thenReturn(mockCourseOfferings);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/course-offerings"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[0].courseNumber").value("COURSE101"))
                .andExpect(jsonPath("$[0].notes").value("Notes 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Course 2"))
                .andExpect(jsonPath("$[1].courseNumber").value("COURSE202"))
                .andExpect(jsonPath("$[1].notes").value("Notes 2"));

        // Verify that courseOfferingRepository.findAll() was called once
        verify(courseOfferingRepository, times(1)).findAll();
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testReadListByQueryExceptionCase() throws Exception {
        // Mock the courseOfferingRepository to throw an exception when findAll() is called
        when(courseOfferingRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/course-offerings"))
                .andExpect(status().isBadRequest());

        // Verify that courseOfferingRepository.findAll() was called once
        verify(courseOfferingRepository, times(1)).findAll();
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testReadOneById() throws Exception {
        // Mock the data returned by the repository
        CourseOffering mockCourseOffering = new CourseOffering(1, "Course 1", "COURSE101", "Notes 1",
                new HashSet<>(), new HashSet<>());
        when(courseOfferingRepository.findById(1)).thenReturn(Optional.of(mockCourseOffering));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/course-offerings/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Course 1"))
                .andExpect(jsonPath("$.courseNumber").value("COURSE101"))
                .andExpect(jsonPath("$.notes").value("Notes 1"));

        // Verify that courseOfferingRepository.findById() was called once with the
        // correct ID
        verify(courseOfferingRepository, times(1)).findById(1);
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testReadOneByIdExceptionCase() throws Exception {

        // Mock the courseOfferingRepository to throw an exception when findById() is called with an invalid ID
        when(courseOfferingRepository.findById(1)).thenThrow(new RuntimeException("Course Offering not found"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/course-offerings/{id}", 1))
                .andExpect(status().isBadRequest());

        // Verify that courseOfferingRepository.findById() was called once with the correct ID
        verify(courseOfferingRepository, times(1)).findById(1);
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testCreateOrUpdateList() throws Exception {
        // Mock the data to be sent in the request
        List<CourseOfferingDto> courseOfferingDtoList = new ArrayList<>();
        courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 1", "COURSE101", "Notes 1", new ArrayList<>(), new ArrayList<>()));
        courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 2", "COURSE202", "Notes 2", new ArrayList<>(), new ArrayList<>()));

        // Mock the data returned by the repository after saving
        CourseOffering savedCourseOffering1 = new CourseOffering(1, "Course 1", "COURSE101", "Notes 1", new HashSet<>(),
                new HashSet<>());
        CourseOffering savedCourseOffering2 = new CourseOffering(2, "Course 2", "COURSE202", "Notes 2", new HashSet<>(),
                new HashSet<>());
        when(courseOfferingRepository.save(any(CourseOffering.class)))
                .thenReturn(savedCourseOffering1)
                .thenReturn(savedCourseOffering2);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/course-offerings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseOfferingDtoList)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Course 1"))
                .andExpect(jsonPath("$[0].courseNumber").value("COURSE101"))
                .andExpect(jsonPath("$[0].notes").value("Notes 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Course 2"))
                .andExpect(jsonPath("$[1].courseNumber").value("COURSE202"))
                .andExpect(jsonPath("$[1].notes").value("Notes 2"));

        // Verify that courseOfferingRepository.save() was called twice with the correct
        // data
        ArgumentCaptor<CourseOffering> courseOfferingArgumentCaptor = ArgumentCaptor.forClass(CourseOffering.class);
        verify(courseOfferingRepository, times(2)).save(courseOfferingArgumentCaptor.capture());
        verifyNoMoreInteractions(courseOfferingRepository);

        // Verify the data passed to the repository
        List<CourseOffering> savedCourseOfferings = courseOfferingArgumentCaptor.getAllValues();
        assertEquals("Course 1", savedCourseOfferings.get(0).getName());
        assertEquals("COURSE101", savedCourseOfferings.get(0).getCourseNumber());
        assertEquals("Notes 1", savedCourseOfferings.get(0).getNotes());

        assertEquals("Course 2", savedCourseOfferings.get(1).getName());
        assertEquals("COURSE202", savedCourseOfferings.get(1).getCourseNumber());
        assertEquals("Notes 2", savedCourseOfferings.get(1).getNotes());
    }

    @Test
    public void testCreateOrUpdateListExceptionCase() throws Exception {
        // Create a list of courseOffering DTOs with null values for required fields
        List<CourseOfferingDto> courseOfferingDtoList = new ArrayList<>();
        courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 1", "COURSE101", "Notes 1", new ArrayList<>(), new ArrayList<>()));
        courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 2", "COURSE202", "Notes 2", new ArrayList<>(), new ArrayList<>()));

        when(courseOfferingRepository.save(any(CourseOffering.class))).thenThrow(new RuntimeException(""));

        // Perform the request and verify the response
        mockMvc.perform(post("/api/course-offerings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseOfferingDtoList)))
                .andExpect(status().isBadRequest());

        // Verify that courseOfferingRepository.save() was not called
        verify(courseOfferingRepository, times(1)).save(any());
    }

    @Test
    public void testUpdateOneById() throws Exception {
        // Mock the data to be sent in the request
        CourseOfferingDto courseOfferingDto = new CourseOfferingDto(1, "Updated Course", "COURSE303",
                "Updated Notes", new ArrayList<>(), new ArrayList<>());

        // Mock the data returned by the repository after updating
        CourseOffering updatedCourseOffering = new CourseOffering(1, "Updated Course", "COURSE303",
                "Updated Notes", new HashSet<>(), new HashSet<>());
        when(courseOfferingRepository.findById(1)).thenReturn(Optional.of(updatedCourseOffering));
        when(courseOfferingRepository.save(any(CourseOffering.class))).thenReturn(updatedCourseOffering);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/course-offerings/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseOfferingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Course"))
                .andExpect(jsonPath("$.courseNumber").value("COURSE303"))
                .andExpect(jsonPath("$.notes").value("Updated Notes"));

        // Verify that courseOfferingRepository.findById() and
        // courseOfferingRepository.save() were called once with the correct data
        verify(courseOfferingRepository, times(1)).findById(1);
        verify(courseOfferingRepository, times(1)).save(any(CourseOffering.class));
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testUpdateOneByIdExceptionCase() throws Exception {
        // Mock the courseOfferingRepository.findById() to return a course offering with
        // id=1
        CourseOffering existingCourseOffering = new CourseOffering(1, "Course 1", "CS101", "Intro", null, null);
        when(courseOfferingRepository.findById(1)).thenReturn(Optional.of(existingCourseOffering));

        // Create a courseOffering DTO with a different id than the one in the path
        CourseOfferingDto courseOfferingDto = new CourseOfferingDto(2, "Updated Course 1", "CS101", "Intro", null,
                null);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/course-offerings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(courseOfferingDto)))
                .andExpect(status().isBadRequest());

        verify(courseOfferingRepository, times(0)).findById(1);
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testDeleteOneById() throws Exception {
        // Perform the request and verify the response
        mockMvc.perform(delete("/api/course-offerings/{id}", 1))
                .andExpect(status().isOk());

        // Verify that courseOfferingRepository.deleteById() was called once with the
        // correct ID
        verify(courseOfferingRepository, times(1)).deleteById(1);
        verifyNoMoreInteractions(courseOfferingRepository);
    }

    @Test
    public void testDeleteOneByIdExceptionCase() throws Exception {

        doThrow(IllegalArgumentException.class).when(courseOfferingRepository).deleteById(any(Integer.class));

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/course-offerings/{id}", 1))
                .andExpect(status().isBadRequest());

        verify(courseOfferingRepository, times(1)).deleteById(any(Integer.class));
        verifyNoMoreInteractions(courseOfferingRepository);
    }

}
