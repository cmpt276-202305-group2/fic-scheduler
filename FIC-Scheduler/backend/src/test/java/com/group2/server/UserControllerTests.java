package com.group2.server;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.HashSet;
import java.util.Optional;

import com.group2.server.model.ApplicationUser;
import com.group2.server.model.Role;
import com.group2.server.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {

        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private UserRepository userRepository;

        // @BeforeEach // Create a mock user before each test
        // void setup() {
        // }

        ApplicationUser makeMockUser(String username, String password, Role role) {
                var roles = new HashSet<Role>();
                if (role != null) {
                        roles.add(role);
                }
                return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
        }

        @Test // Test the login endpoint with a user that does exists in the database
        public void testLoginFailMissingUser() throws Exception {
                var mockAdmin = makeMockUser("testUsername", "testPassword", Role.ADMIN);
                when(userRepository.findByUsername(anyString()))
                                .thenReturn(Optional.empty());
                when(userRepository.findByUsername(mockAdmin.getUsername()))
                                .thenReturn(Optional.ofNullable(mockAdmin));
                mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"missingUser\", \"password\":\"testPassword\"}"))
                                .andExpect(MockMvcResultMatchers.status().isForbidden())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user").isEmpty())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isEmpty())
                                .andDo(print());
        }

        @Test // Test the login endpoint with a user that does exists in the database
        public void testLoginFailBadPassword() throws Exception {
                var mockAdmin = makeMockUser("testUsername", "testPassword", Role.ADMIN);
                when(userRepository.findByUsername(anyString()))
                                .thenReturn(Optional.empty());
                when(userRepository.findByUsername(mockAdmin.getUsername()))
                                .thenReturn(Optional.ofNullable(mockAdmin));
                mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testUsername\", \"password\":\"badPassword\"}"))
                                .andExpect(MockMvcResultMatchers.status().isForbidden())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user").isEmpty())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isEmpty())
                                .andDo(print());
        }

        @Test // Test the login endpoint with a user that does exists in the database with the
              // role Admin
        public void testLoginSuccessAdmin() throws Exception {
                var mockAdmin = makeMockUser("testUsername", "testPassword", Role.ADMIN);
                when(userRepository.findByUsername(anyString()))
                                .thenReturn(Optional.empty());
                when(userRepository.findByUsername(mockAdmin.getUsername()))
                                .thenReturn(Optional.ofNullable(mockAdmin));
                mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testUsername\", \"password\":\"testPassword\"}"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value("testUsername"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.roles[0]").value("ADMIN"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isNotEmpty())
                                .andDo(print());
        }

        @Test // Test the login endpoint with a user that does exists in the database with the
              // role professor
        public void testLoginSuccessCoordinator() throws Exception {
                ApplicationUser mockCoordinator = makeMockUser("testUsername", "testPassword", Role.COORDINATOR);
                when(userRepository.findByUsername(anyString()))
                                .thenReturn(Optional.empty());
                when(userRepository.findByUsername(mockCoordinator.getUsername()))
                                .thenReturn(Optional.ofNullable(mockCoordinator));
                mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testUsername\", \"password\":\"testPassword\"}"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value("testUsername"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.roles[0]").value("COORDINATOR"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isNotEmpty())
                                .andDo(print());
        }

        @Test // Test the login endpoint with a user that does exists in the database with the
              // role professor
        public void testLoginSuccessInstructor() throws Exception {
                ApplicationUser mockInstructor = makeMockUser("testUsername", "testPassword", Role.INSTRUCTOR);
                when(userRepository.findByUsername(anyString()))
                                .thenReturn(Optional.empty());
                when(userRepository.findByUsername(mockInstructor.getUsername()))
                                .thenReturn(Optional.ofNullable(mockInstructor));
                mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"username\":\"testUsername\", \"password\":\"testPassword\"}"))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.username").value("testUsername"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.user.roles[0]").value("INSTRUCTOR"))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.jwt").isNotEmpty())
                                .andDo(print());
        }

}
