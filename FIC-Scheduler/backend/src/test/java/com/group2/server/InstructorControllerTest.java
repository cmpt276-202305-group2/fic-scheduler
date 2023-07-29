package com.group2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.InstructorController;
import com.group2.server.dto.InstructorDto;
import com.group2.server.model.Instructor;
import com.group2.server.repository.InstructorRepository;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class InstructorControllerTest {

    @InjectMocks
    private InstructorController instructorController;

    @Mock
    private InstructorRepository instructorRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(instructorController).build();
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
        List<Instructor> mockInstructors = new ArrayList<>();
        mockInstructors.add(new Instructor(1, "John Doe", "Mathematics"));
        mockInstructors.add(new Instructor(2, "Jane Smith", "Physics"));
        when(instructorRepository.findAll()).thenReturn(mockInstructors);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/instructors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].notes").value("Mathematics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].notes").value("Physics"));

        // Verify that instructorRepository.findAll() was called once
        verify(instructorRepository, times(1)).findAll();
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testReadListByQueryWithBadRequest() throws Exception {
        // Mock the instructorRepository to throw a custom exception when findAll() is called
        when(instructorRepository.findAll()).thenThrow(new RuntimeException("Something went wrong"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/instructors"))
                .andExpect(status().isBadRequest());

        // Verify that instructorRepository.findAll() was called once
        verify(instructorRepository, times(1)).findAll();
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testReadOneById() throws Exception {
        // Mock the data returned by the repository
        Instructor mockInstructor = new Instructor(1, "John Doe", "Mathematics");
        when(instructorRepository.findById(1)).thenReturn(Optional.of(mockInstructor));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/instructors/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.notes").value("Mathematics"));

        // Verify that instructorRepository.findById() was called once
        verify(instructorRepository, times(1)).findById(1);
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testReadOneByIdNotFound() throws Exception {
        int instructorId = 1;

        // Mock the instructorRepository to return an empty optional when findById() is called
        when(instructorRepository.findById(instructorId)).thenThrow(new RuntimeException("Instructor Not found"));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/instructors/{id}", instructorId))
                .andExpect(status().isBadRequest());

        // Verify that instructorRepository.findById() was called once with the correct ID
        verify(instructorRepository, times(1)).findById(instructorId);
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testCreateOrUpdateList() throws Exception {
        // Mock the data to be sent in the request
        List<InstructorDto> instructorDtoList = new ArrayList<>();
        instructorDtoList.add(new InstructorDto(null, "John Doe", "Mathematics"));
        instructorDtoList.add(new InstructorDto(null, "Jane Smith", "Physics"));

        Instructor savedInstructor_1 = new Instructor(1, "John Doe", "Mathematics");
        Instructor savedInstructor_2 = new Instructor(2, "Jane Smith", "Physics");
        when(instructorRepository.save(any(Instructor.class)))
                .thenReturn(savedInstructor_1)
                .thenReturn(savedInstructor_2);

        // Perform the request and verify the response
        mockMvc.perform(post("/api/instructors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(instructorDtoList)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].notes").value("Mathematics"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$[1].notes").value("Physics"));

        // Verify that instructorRepository.save() was called twice
        verify(instructorRepository, times(2)).save(any(Instructor.class));
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testCreateOrUpdateListBadRequest() throws Exception {
        // Mock the data to be sent in the request
        List<InstructorDto> instructorDtoList = new ArrayList<>();
        instructorDtoList.add(new InstructorDto(null, null, "Notes"));
        instructorDtoList.add(new InstructorDto(null, "Jane Smith", null));

        // Perform the request and verify the response
        mockMvc.perform(post("/api/instructors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(instructorDtoList)))
                .andExpect(status().isBadRequest());

        // Verify that instructorRepository.saveAll() was called once with the correct list of instructors
        verify(instructorRepository, times(1)).save(any());
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testUpdateOneById() throws Exception {
        // Mock the updated data
        InstructorDto updatedDto = new InstructorDto(1, "Jane Smith", "Chemistry");

        // Mock the instructor data in the repository before updating
        Instructor existingInstructor = new Instructor(1, "John Doe", "Mathematics");
        when(instructorRepository.findById(1)).thenReturn(Optional.of(existingInstructor));

        // Mock the data returned by the repository after updating
        Instructor updatedInstructor = new Instructor(1, "Jane Smith", "Chemistry");
        when(instructorRepository.save(any(Instructor.class))).thenReturn(updatedInstructor);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/instructors/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.notes").value("Chemistry"));

        // Verify that instructorRepository.findById() and instructorRepository.save() were called once
        verify(instructorRepository, times(1)).findById(1);

        ArgumentCaptor<Instructor> InstructorCaptor = ArgumentCaptor.forClass(Instructor.class);
        verify(instructorRepository, times(1)).save(InstructorCaptor.capture());
        verifyNoMoreInteractions(instructorRepository);

        // Verify the updated Instructor data
        Instructor capturedInstructor = InstructorCaptor.getValue();
        assertEquals(1, capturedInstructor.getId());
        assertEquals("Jane Smith", capturedInstructor.getName());
        assertEquals("Chemistry", capturedInstructor.getNotes());
    }

    @Test
    public void testUpdateOneByIdBadRequest() throws Exception {
        int instructorId = 1;
        InstructorDto instructorDto = new InstructorDto(2, "John Doe", "Notes");

        // Perform the request and verify the response
        mockMvc.perform(put("/api/instructors/{id}", instructorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(instructorDto)))
                .andExpect(status().isBadRequest());

        // Verify that instructorRepository.findById() was not called because of the mismatch in the ID
        verify(instructorRepository, times(0)).findById(instructorId);
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testDeleteOneById() throws Exception {

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/instructors/{id}", 1))
                .andExpect(status().isOk());

        // Verify that instructorRepository.deleteById() was called once
        verify(instructorRepository, times(1)).deleteById(1);
        verifyNoMoreInteractions(instructorRepository);
    }

    @Test
    public void testDeleteOneByIdExceptionCase() throws Exception {

        doThrow(IllegalArgumentException.class).when(instructorRepository).deleteById(any(Integer.class));

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/instructors/{id}", 1))
                .andExpect(status().isBadRequest());

        verify(instructorRepository, times(1)).deleteById(any(Integer.class));
        verifyNoMoreInteractions(instructorRepository);
    }

}
