package com.group2.server;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group2.server.controller.InstructorController;
import com.group2.server.dto.InstructorDto;
import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Instructor;
import com.group2.server.model.Role;
import com.group2.server.repository.BlockRepository;
import com.group2.server.repository.InstructorRepository;
import com.group2.server.services.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@WebMvcTest(InstructorController.class)
public class InstructorControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstructorRepository instructorRepository;

    // @MockBean
    // private BlockRepository blockRepository;

    private Instructor mockInstructor;
    private InstructorDto mockInstructorDto;
    private List<Instructor> mockInstructorList;
    private ApplicationUser mockUser;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void setup() {
        //create a mock user for authentication
         mockUser = makeMockUser("testUser", "testPassword", Role.INSTRUCTOR);

        // Create a mock instructor
        mockInstructor = new Instructor(1, "John Doe");
        // Create a mock instructor DTO
        mockInstructorDto = new InstructorDto(1, "John Doe");
        // Create a list of mock instructors
        mockInstructorList = new ArrayList<>();
        mockInstructorList.add(mockInstructor);
    }

    private ApplicationUser makeMockUser(String username, String password, Role role) {
        var roles = new HashSet<Role>();
        if (role != null) {
            roles.add(role);
        }

        return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
    }


    @Test
    public void testGetAllInstructors() throws Exception {
        // Mock the behavior of the instructorRepository.findAll() method
        when(instructorRepository.findAll()).thenReturn(mockInstructorList);

        mockMvc.perform(get("/api/instructors"))
                // .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockInstructorDto.getId()))
                .andExpect(jsonPath("$[0].name").value(mockInstructorDto.getName()));
    }
}
