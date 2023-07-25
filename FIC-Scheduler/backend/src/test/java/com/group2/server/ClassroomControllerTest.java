package com.group2.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.ClassroomController;
import com.group2.server.dto.ClassroomDto;
import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Classroom;
import com.group2.server.model.Role;
import com.group2.server.repository.ClassroomRepository;
import com.group2.server.services.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class ClassroomControllerTest {

    @InjectMocks
    private ClassroomController classroomController;

    @Mock
    private ClassroomRepository classroomRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(classroomController).build();
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
        List<Classroom> mockClassrooms = new ArrayList<>();
        mockClassrooms.add(new Classroom(1, "101", "Lecture Hall", "First floor"));
        mockClassrooms.add(new Classroom(2, "202", "Lab", "Second floor"));
        when(classroomRepository.findAll()).thenReturn(mockClassrooms);

        // Perform the request and verify the response
        mockMvc.perform(get("/api/classrooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].roomNumber").value("101"))
                .andExpect(jsonPath("$[0].roomType").value("Lecture Hall"))
                .andExpect(jsonPath("$[0].notes").value("First floor"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].roomNumber").value("202"))
                .andExpect(jsonPath("$[1].roomType").value("Lab"))
                .andExpect(jsonPath("$[1].notes").value("Second floor"));

        // Verify that classroomRepository.findAll() was called once
        verify(classroomRepository, times(1)).findAll();
        verifyNoMoreInteractions(classroomRepository);
    }

    @Test
    public void testReadOneById() throws Exception {
        // Mock the data returned by the repository
        Classroom mockClassroom = new Classroom(1, "101", "Lecture Hall", "First floor");
        when(classroomRepository.findById(1)).thenReturn(Optional.of(mockClassroom));

        // Perform the request and verify the response
        mockMvc.perform(get("/api/classrooms/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomNumber").value("101"))
                .andExpect(jsonPath("$.roomType").value("Lecture Hall"))
                .andExpect(jsonPath("$.notes").value("First floor"));

        // Verify that classroomRepository.findById() was called once with the correct ID
        verify(classroomRepository, times(1)).findById(1);
        verifyNoMoreInteractions(classroomRepository);
    }

    // @Test
    // public void testCreateOrUpdateList() throws Exception {
    //     // Mock the data to be sent in the request
    //     // Mock the data to be sent in the request
    //     List<ClassroomDto> classroomDtoList = new ArrayList<>();
    //     classroomDtoList.add(new ClassroomDto(null, "101", "Lecture Hall", "First floor"));
    //     classroomDtoList.add(new ClassroomDto(null, "202", "Lab", "Second floor"));

    //     // Mock the data returned by the repository after saving
    //     List<Classroom> savedClassrooms = new ArrayList<>();
    //     savedClassrooms.add(new Classroom(1, "101", "Lecture Hall", "First floor"));
    //     savedClassrooms.add(new Classroom(2, "202", "Lab", "Second floor"));
    //     when(classroomRepository.saveAll(anyList())).thenReturn(savedClassrooms);

    //     // Perform the request and verify the response
    //     mockMvc.perform(post("/api/classrooms")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(asJsonString(classroomDtoList)))
    //             .andExpect(status().isOk())
    //             .andExpect(content().contentType(MediaType.APPLICATION_JSON))
    //             .andExpect(jsonPath("$[0].id").value(1))
    //             .andExpect(jsonPath("$[0].roomNumber").value("101"))
    //             .andExpect(jsonPath("$[0].roomType").value("Lecture Hall"))
    //             .andExpect(jsonPath("$[0].notes").value("First floor"))
    //             .andExpect(jsonPath("$[1].id").value(2))
    //             .andExpect(jsonPath("$[1].roomNumber").value("202"))
    //             .andExpect(jsonPath("$[1].roomType").value("Lab"))
    //             .andExpect(jsonPath("$[1].notes").value("Second floor"));

    //     // Verify that classroomRepository.saveAll() was called once with the correct list of classrooms
    //     verify(classroomRepository, times(1)).saveAll(anyList());
    //     verifyNoMoreInteractions(classroomRepository);
    // }

    @Test
    public void testUpdateOneById() throws Exception {
        // Mock the updated data
        ClassroomDto updatedDto = new ClassroomDto(1, "303", "Seminar Room", "Third floor");

        // Mock the classroom data in the repository before updating
        Classroom existingClassroom = new Classroom(1, "101", "Lecture Hall", "First floor");
        when(classroomRepository.findById(1)).thenReturn(Optional.of(existingClassroom));

        // Mock the data returned by the repository after updating
        Classroom updatedClassroom = new Classroom(1, "303", "Seminar Room", "Third floor");
        when(classroomRepository.save(any(Classroom.class))).thenReturn(updatedClassroom);

        // Perform the request and verify the response
        mockMvc.perform(put("/api/classrooms/{id}", 1)
                // .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.roomNumber").value("303"))
                .andExpect(jsonPath("$.roomType").value("Seminar Room"))
                .andExpect(jsonPath("$.notes").value("Third floor"));

        // Verify that classroomRepository.findById() was called once with the correct ID
        verify(classroomRepository, times(1)).findById(1);

        // Verify that classroomRepository.save() was called once with the updated classroom
        ArgumentCaptor<Classroom> classroomCaptor = ArgumentCaptor.forClass(Classroom.class);
        verify(classroomRepository, times(1)).save(classroomCaptor.capture());
        verifyNoMoreInteractions(classroomRepository);

        // Verify the updated classroom data
        Classroom capturedClassroom = classroomCaptor.getValue();
        assertEquals(1, capturedClassroom.getId());
        assertEquals("303", capturedClassroom.getRoomNumber());
        assertEquals("Seminar Room", capturedClassroom.getRoomType());
        assertEquals("Third floor", capturedClassroom.getNotes());
    }


    @Test
    public void testDeleteOneById() throws Exception {
        // Mock the data to be deleted
        Classroom deletedClassroom = new Classroom(1,"101","Lecture Hall", "First Floor");
        when(classroomRepository.findById(1)).thenReturn(Optional.of(deletedClassroom));

        // Perform the request and verify the response
        mockMvc.perform(delete("/api/classrooms/{id}", 1))
                .andExpect(status().isOk());

        verify(classroomRepository, times(1)).deleteById(1);
        verifyNoMoreInteractions(classroomRepository);
    }
}