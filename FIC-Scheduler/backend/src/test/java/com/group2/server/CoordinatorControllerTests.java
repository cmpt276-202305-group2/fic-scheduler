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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.Optional;
import java.util.List;
import java.lang.reflect.Field;

import com.group2.server.model.ClassSchedule;
import com.group2.server.model.SemesterPlan;
import com.group2.server.repository.ClassScheduleRepository;
import com.group2.server.repository.SemesterPlanRepository;
import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Role;

@SpringBootTest
@AutoConfigureMockMvc
public class CoordinatorControllerTests {
    //Not Working at the moment but wanted to commit the update.
    @Autowired
    private MockMvc mockMvc;

    private ApplicationUser mockUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        // Create a mock user for authentication
        mockUser = makeMockUser("testUser", "testPassword", Role.COORDINATOR);
    }

    // Mock a user for authentication
    private ApplicationUser makeMockUser(String username, String password, Role role) {
        var roles = new HashSet<Role>();
        if (role != null) {
            roles.add(role);
        }
        return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
    }

    @Test
    public void testVerifiedCoordinatorController() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/coordinator")
                .with(SecurityMockMvcRequestPostProcessors.user(mockUser)) // Include the mock user for authentication
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("COORDINATOR level access BABYY"))
                .andDo(print());
    }
}