// package com.group2.server;

// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Set;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.group2.server.controller.InstructorDto;
// import com.group2.server.model.Accreditation;
// import com.group2.server.model.Instructor;
// import com.group2.server.repository.AccreditationRepository;
// import com.group2.server.services.TokenService;
// import com.group2.server.model.ApplicationUser;
// import com.group2.server.model.InstructorRepository;
// import com.group2.server.model.Role;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class InstructorControllerTests {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private InstructorRepository instructorRepository;

//     @MockBean
//     private AccreditationRepository accreditationRepository;

//     @Autowired
//     private PasswordEncoder passwordEncoder;
//     @Autowired
//     private TokenService tokenService;

//     private String mockJwt;

//     private ApplicationUser mockUser;

//     @BeforeEach
//     public void setup() {
//         // Create a mock user for authentication
//         mockUser = makeMockUser("testUser", "testPassword", Role.INSTRUCTOR);

//     }

//     // Mock a user for authentication
//     private ApplicationUser makeMockUser(String username, String password, Role role) {
//         var roles = new HashSet<Role>();
//         if (role != null) {
//             roles.add(role);
//         }
    
//         ApplicationUser user = new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
        
//         // Generate JWT token for the mock user
//         mockJwt = tokenService.generateJwt(username, roles);
    
//         return user;
//     }

//     @Test
//     public void testCreateInstructors() throws Exception {
//         // Mock the data returned by the repository
//         when(accreditationRepository.findByName("Accreditation1")).thenReturn(new Accreditation(1, "Accreditation1"));
//         when(accreditationRepository.findByName("Accreditation2")).thenReturn(new Accreditation(2, "Accreditation2"));
//         when(instructorRepository.findByName("Instructor1")).thenReturn(null);
//         when(instructorRepository.findByName("Instructor2")).thenReturn(null);

//         // Prepare the request body
//         InstructorDto instructorDto1 = new InstructorDto("Instructor1", Set.of("Accreditation1"));
//         InstructorDto instructorDto2 = new InstructorDto("Instructor2", Set.of("Accreditation2"));
//         List<InstructorDto> instructorDtos = List.of(instructorDto1, instructorDto2);

//         // Perform the request
//         mockMvc.perform(post("/api/instructors")
//                 .header("Authorization", "Bearer " + mockJwt)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(new ObjectMapper().writeValueAsString(instructorDtos))
//                 .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
//                 .andExpect(status().isCreated())
//                 .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2))
//                 .andDo(print());
//     }
// }