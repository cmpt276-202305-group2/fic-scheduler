// package com.group2.server;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.ArgumentMatchers.anyInt;
// import static org.mockito.Mockito.mock;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.group2.server.controller.InstructorController;
// import com.group2.server.dto.InstructorDto;
// import com.group2.server.model.ApplicationUser;
// import com.group2.server.model.Instructor;
// import com.group2.server.model.Role;
// import com.group2.server.repository.BlockRequirementSplitRepository;
// import com.group2.server.repository.InstructorRepository;
// import com.group2.server.services.TokenService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;

// @WebMvcTest(InstructorController.class)
// public class InstructorControllerTests {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private InstructorRepository instructorRepository;

//     // @MockBean
//     // private BlockRepository blockRepository;

//     private Instructor mockInstructor;
//     private InstructorDto mockInstructorDto;
//     private List<Instructor> mockInstructorList;
//     private ApplicationUser mockUser;

//     @Autowired
//     private ObjectMapper objectMapper;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @BeforeEach
//     public void setup() {
//         // create a mock user for authentication
//         mockUser = makeMockUser("testUser", "testPassword", Role.INSTRUCTOR);

//         // Create a mock instructor
//         mockInstructor = mock(Instructor.class);
//         when(mockInstructor.getId()).thenReturn(1);
//         when(mockInstructor.getName()).thenReturn("John Doe");

//         // Create a mock instructor DTO
//         mockInstructorDto = new InstructorDto();
//         mockInstructorDto.setId(1);
//         mockInstructorDto.setName("John Doe");

//         // Create a list of mock instructors
//         mockInstructorList = new ArrayList<>();
//         mockInstructorList.add(mockInstructor);
//     }

//     private ApplicationUser makeMockUser(String username, String password, Role role) {
//         var roles = new HashSet<Role>();
//         if (role != null) {
//             roles.add(role);
//         }

//         return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
//     }

//     @Test
//     public void testGetAllInstructors() throws Exception {
//         // Mock the behavior of the instructorRepository.findAll() method
//         when(instructorRepository.findAll()).thenReturn(mockInstructorList);

//         mockMvc.perform(get("/api/instructors"))
//                 // .with(SecurityMockMvcRequestPostProcessors.user(mockUser))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.length()").value(1))
//                 .andExpect(jsonPath("$[0].id").value(mockInstructorDto.getId()))
//                 .andExpect(jsonPath("$[0].name").value(mockInstructorDto.getName()));
//     }

//     @Test
//     public void testGetInstructorById() throws Exception {
//         when(instructorRepository.findById(anyInt())).thenReturn(Optional.of(mockInstructor));

//         mockMvc.perform(get("/api/instructors/{id}", mockInstructor.getId()))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(mockInstructorDto.getId()))
//                 .andExpect(jsonPath("$.name").value(mockInstructorDto.getName()));
//     }

//     @Test
//     public void testGetInstructorByIdNotFound() throws Exception {
//         when(instructorRepository.findById(anyInt())).thenReturn(Optional.empty());

//         mockMvc.perform(get("/api/instructors/{id}", 999))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void testCreateOrUpdateInstructors() throws Exception {
//         List<InstructorDto> instructorDtoList = new ArrayList<>();
//         instructorDtoList.add(new InstructorDto(null, "Jane Smith"));
//         instructorDtoList.add(mockInstructorDto);

//         when(instructorRepository.saveAll(any())).thenReturn(mockInstructorList);

//         mockMvc.perform(post("/api/instructors")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(instructorDtoList)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.length()").value(2))
//                 .andExpect(jsonPath("$[0].id").value(1))
//                 .andExpect(jsonPath("$[0].name").value("Jane Smith"))
//                 .andExpect(jsonPath("$[1].id").value(mockInstructorDto.getId()))
//                 .andExpect(jsonPath("$[1].name").value(mockInstructorDto.getName()));
//     }

//     @Test
//     public void testUpdateInstructorById() throws Exception {
//         InstructorDto updatedInstructorDto = new InstructorDto(1, "Updated Instructor");

//         when(instructorRepository.findById(anyInt())).thenReturn(Optional.of(mockInstructor));
//         when(instructorRepository.save(any())).thenReturn(mockInstructor);

//         mockMvc.perform(put("/api/instructors/{id}", mockInstructor.getId())
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updatedInstructorDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(mockInstructorDto.getId()))
//                 .andExpect(jsonPath("$.name").value(updatedInstructorDto.getName()));
//     }

//     @Test
//     public void testUpdateInstructorByIdNotFound() throws Exception {
//         InstructorDto updatedInstructorDto = new InstructorDto(999, "Updated Instructor");

//         when(instructorRepository.findById(anyInt())).thenReturn(Optional.empty());

//         mockMvc.perform(put("/api/instructors/{id}", 999)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(objectMapper.writeValueAsString(updatedInstructorDto)))
//                 .andExpect(status().isNotFound());
//     }

//     @Test
//     public void testDeleteInstructorById() throws Exception {
//         when(instructorRepository.existsById(anyInt())).thenReturn(true);

//         mockMvc.perform(delete("/api/instructors/{id}", mockInstructor.getId()))
//                 .andExpect(status().isOk());
//     }

//     @Test
//     public void testDeleteInstructorByIdNotFound() throws Exception {
//         when(instructorRepository.existsById(anyInt())).thenReturn(false);

//         mockMvc.perform(delete("/api/instructors/{id}", 999))
//                 .andExpect(status().isNotFound());
//     }
// }
