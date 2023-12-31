package com.group2.server;

import static org.mockito.ArgumentMatchers.any;
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

import java.util.*;

import com.group2.server.model.*;
import com.group2.server.repository.*;
import com.group2.server.services.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GenerateScheduleControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private SemesterPlanRepository semesterPlanRepository;

    private SemesterPlan mockPlan;
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

        // Create a mock plan
        try {
            mockPlan = makeMockPlan("Fall 2023");
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

    private SemesterPlan makeMockPlan(String semester) throws Exception {
        return new SemesterPlan(1, "", "", semester, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(),
                new HashSet<>());
    }

    @Test
    public void testGenerateSchedule() throws Exception {
        //Mock the repository call
        when(semesterPlanRepository.findById(1)).thenReturn(Optional.ofNullable(mockPlan));
        when(scheduleRepository.save(any())).thenAnswer(
            i -> {
                var sched = (Schedule)i.getArgument(0);
                return sched;
            });

        // Perform the request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/generate-schedule")
                .header("Authorization", "Bearer " + mockJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"semesterPlan\": { \"id\": 1 } }")
                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.semester").value(mockPlan.getSemester()))
                .andDo(print());
    }
}