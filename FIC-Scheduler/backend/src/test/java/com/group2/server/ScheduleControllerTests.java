package com.group2.server;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.Optional;
import java.util.List;

import com.group2.server.model.Schedule;
import com.group2.server.repository.ScheduleRepository;
import com.group2.server.repository.SemesterPlanRepository;
import com.group2.server.services.TokenService;
import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Role;

@SpringBootTest
@AutoConfigureMockMvc
public class ScheduleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private SemesterPlanRepository semesterPlanRepository;

    private Schedule mockSchedule;
    private ApplicationUser mockUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private String mockJwt;

    @BeforeEach
    public void setup() {
        // Create a mock user for authentication
        mockUser = makeMockUser("testUser", "testPassword", Role.COORDINATOR);

        try {
            // Create a mock schedule
            mockSchedule = makeMockSchedule("Fall 2023");
        } catch (Exception e) {
            // Handle the exception if needed
            e.printStackTrace();
        }

    }

    // Mock a user for authentication
    private ApplicationUser makeMockUser(String username, String password, Role role) {
        var roles = new HashSet<Role>();
        if (role != null) {
            roles.add(role);
        }

        mockJwt = tokenService.generateJwt(username, roles);

        return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
    }

    private Schedule makeMockSchedule(String semester) throws Exception {
        return new Schedule(1, "", "", semester, new HashSet<>());
    }

    @Test
    public void testGetLatestSchedule() throws Exception {
        // Mock the data returned by the repository
        when(scheduleRepository.findAll()).thenReturn(List.of(mockSchedule));

        // Perform the request
        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedules/latest")
                .header("Authorization", "Bearer " + mockJwt)
                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)) // Include the mock user for authentication
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockSchedule.getSemester()))
                .andDo(print());
    }

    @Test
    public void testGetScheduleById() throws Exception {
        // Mock the data returned by the repository
        int scheduleId = 1;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.ofNullable(mockSchedule));

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedules/{id}", scheduleId)
                .header("Authorization", "Bearer " + mockJwt)
                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockSchedule.getSemester()))
                // ... verify other properties of the response
                .andDo(print());
    }

    @Test
    public void testGetSchedulesByQuery() throws Exception {
        // Mock the data returned by the repository
        when(scheduleRepository.findBySemester(anyString())).thenReturn(new HashSet<>(List.of(mockSchedule)));
    
        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/schedules").param("semester", "Fall 2023")
                .header("Authorization", "Bearer " + mockJwt)
                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].semester").value(mockSchedule.getSemester()))
                .andDo(print());
    }

}