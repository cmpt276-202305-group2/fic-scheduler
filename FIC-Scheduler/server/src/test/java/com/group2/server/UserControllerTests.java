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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


import com.group2.server.Model.User;
import com.group2.server.Repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setUsername("testUsername");
        mockUser.setPassword("testPassword");
        mockUser.setRole("admin");
    }

    @Test
    public void testLoginSuccessAdmin() throws Exception {
        when(userRepository.findByUsername(mockUser.getUsername())).thenReturn(mockUser);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUsername\", \"password\":\"testPassword\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Login successful"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("admin"))
                .andDo(print());
    }

    @Test
    public void testLoginSuccessProfessor() throws Exception {
    
    User mockProfessor = new User();
    mockProfessor.setUsername("professorUsername");
    mockProfessor.setPassword("professorPassword");
    mockProfessor.setRole("professor");

    when(userRepository.findByUsername(mockProfessor.getUsername())).thenReturn(mockProfessor);

    // Perform the post request and check the response
    mockMvc.perform(MockMvcRequestBuilders.post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"professorUsername\", \"password\":\"professorPassword\"}")
    )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Login successful"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.role").value("professor"))
            .andDo(print());
}

    @Test
    public void testLoginFail() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testUsername\", \"password\":\"testPassword\"}")
        )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    
}
