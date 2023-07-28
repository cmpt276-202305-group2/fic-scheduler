// package com.group2.server;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.group2.server.controller.CourseOfferingController;
// import com.group2.server.dto.CourseOfferingDto;
// // import com.group2.server.model.CourseOffering;
// import com.group2.server.model.*;
// import com.group2.server.repository.*;
// // import com.group2.server.repository.CourseOfferingRepository;
// // import com.group2.server.repository.BlockRequirementSplitRepository;
// // import com.group2.server.repository.InstructorRepository;
// import com.group2.server.services.TokenService;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.ArgumentCaptor;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import java.util.ArrayList;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Optional;
// import java.util.Set;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class CourseOfferingControllerTest {

//     @InjectMocks
//     private CourseOfferingController courseOfferingController;

//     @Mock
//     private CourseOfferingRepository courseOfferingRepository;

//     @Mock
//     private BlockRequirementSplitRepository blockRequirementSplitRepository;

//     @Mock
//     private InstructorRepository instructorRepository;

//     private MockMvc mockMvc;

//     private ApplicationUser mockUser;

//     @Autowired
//     private PasswordEncoder passwordEncoder;

//     @Autowired
//     private TokenService tokenService;

//     private String mockJwt;

//     @BeforeEach
//     public void setup() {
//         mockUser = makeMockUser("testUser", "testPassword", Role.INSTRUCTOR);

//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(courseOfferingController).build();
//     }

//     private ApplicationUser makeMockUser(String username, String password, Role role) {
//         var roles = new HashSet<Role>();
//         if (role != null) {
//             roles.add(role);
//         }

//         mockJwt = tokenService.generateJwt(username, roles);

//         return new ApplicationUser((Integer) 1, username, passwordEncoder.encode(password), roles, "");
//     }

//     private static String asJsonString(final Object obj) {
//         try {
//             return new ObjectMapper().writeValueAsString(obj);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void testReadListByQuery() throws Exception {
//         // Mock the data returned by the repository
//         List<CourseOffering> mockCourseOfferings = new ArrayList<>();
//         mockCourseOfferings.add(new CourseOffering(1, "Course 1", "COURSE101", "Notes 1", null, null));
//         mockCourseOfferings.add(new CourseOffering(2, "Course 2", "COURSE202", "Notes 2", null, null));
//         when(courseOfferingRepository.findAll()).thenReturn(mockCourseOfferings);

//         // Perform the request and verify the response
//         mockMvc.perform(get("/api/course-offerings"))
//                 // .header("Authorization", "Bearer " + mockJwt)
//                 // .with(SecurityMockMvcRequestPostProcessors.user(mockUser)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$[0].id").value(1))
//                 .andExpect(jsonPath("$[0].name").value("Course 1"))
//                 .andExpect(jsonPath("$[0].courseNumber").value("COURSE101"))
//                 .andExpect(jsonPath("$[0].notes").value("Notes 1"))
//                 .andExpect(jsonPath("$[1].id").value(2))
//                 .andExpect(jsonPath("$[1].name").value("Course 2"))
//                 .andExpect(jsonPath("$[1].courseNumber").value("COURSE202"))
//                 .andExpect(jsonPath("$[1].notes").value("Notes 2"));

//         // Verify that courseOfferingRepository.findAll() was called once
//         verify(courseOfferingRepository, times(1)).findAll();
//         verifyNoMoreInteractions(courseOfferingRepository);
//     }

//     @Test
//     public void testReadOneById() throws Exception {
//         // Mock the data returned by the repository
//         CourseOffering mockCourseOffering = new CourseOffering(1, "Course 1", "COURSE101", "Notes 1", null, null);
//         when(courseOfferingRepository.findById(1)).thenReturn(Optional.of(mockCourseOffering));

//         // Perform the request and verify the response
//         mockMvc.perform(get("/api/course-offerings/{id}", 1))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.name").value("Course 1"))
//                 .andExpect(jsonPath("$.courseNumber").value("COURSE101"))
//                 .andExpect(jsonPath("$.notes").value("Notes 1"));

//         // Verify that courseOfferingRepository.findById() was called once with the correct ID
//         verify(courseOfferingRepository, times(1)).findById(1);
//         verifyNoMoreInteractions(courseOfferingRepository);
//     }

//     @Test
//     public void testCreateOrUpdateList() throws Exception {
//         // Mock the data to be sent in the request
//         List<CourseOfferingDto> courseOfferingDtoList = new ArrayList<>();
//         courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 1", "COURSE101", "Notes 1", null, null));
//         courseOfferingDtoList.add(new CourseOfferingDto(null, "Course 2", "COURSE202", "Notes 2", null, null));

//         // Mock the data returned by the repository after saving
//         CourseOffering savedCourseOffering1 = new CourseOffering(1, "Course 1", "COURSE101", "Notes 1", null, null);
//         CourseOffering savedCourseOffering2 = new CourseOffering(2, "Course 2", "COURSE202", "Notes 2", null, null);
//         when(courseOfferingRepository.save(any(CourseOffering.class)))
//                 .thenReturn(savedCourseOffering1)
//                 .thenReturn(savedCourseOffering2);

//         // Perform the request and verify the response
//         mockMvc.perform(post("/api/course-offerings")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(asJsonString(courseOfferingDtoList)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$[0].id").value(1))
//                 .andExpect(jsonPath("$[0].name").value("Course 1"))
//                 .andExpect(jsonPath("$[0].courseNumber").value("COURSE101"))
//                 .andExpect(jsonPath("$[0].notes").value("Notes 1"))
//                 .andExpect(jsonPath("$[1].id").value(2))
//                 .andExpect(jsonPath("$[1].name").value("Course 2"))
//                 .andExpect(jsonPath("$[1].courseNumber").value("COURSE202"))
//                 .andExpect(jsonPath("$[1].notes").value("Notes 2"));

//         // Verify that courseOfferingRepository.save() was called twice with the correct data
//         ArgumentCaptor<CourseOffering> courseOfferingArgumentCaptor = ArgumentCaptor.forClass(CourseOffering.class);
//         verify(courseOfferingRepository, times(2)).save(courseOfferingArgumentCaptor.capture());
//         verifyNoMoreInteractions(courseOfferingRepository);

//         // Verify the data passed to the repository
//         List<CourseOffering> savedCourseOfferings = courseOfferingArgumentCaptor.getAllValues();
//         assertEquals("Course 1", savedCourseOfferings.get(0).getName());
//         assertEquals("COURSE101", savedCourseOfferings.get(0).getCourseNumber());
//         assertEquals("Notes 1", savedCourseOfferings.get(0).getNotes());

//         assertEquals("Course 2", savedCourseOfferings.get(1).getName());
//         assertEquals("COURSE202", savedCourseOfferings.get(1).getCourseNumber());
//         assertEquals("Notes 2", savedCourseOfferings.get(1).getNotes());
//     }

//     @Test
//     public void testUpdateOneById() throws Exception {
//         // Mock the data to be sent in the request
//         CourseOfferingDto courseOfferingDto = new CourseOfferingDto(1, "Updated Course", "COURSE303",
//                 "Updated Notes", null, null);

//         // Mock the data returned by the repository after updating
//         CourseOffering updatedCourseOffering = new CourseOffering(1, "Updated Course", "COURSE303",
//                 "Updated Notes", null, null);
//         when(courseOfferingRepository.findById(1)).thenReturn(Optional.of(updatedCourseOffering));
//         when(courseOfferingRepository.save(any(CourseOffering.class))).thenReturn(updatedCourseOffering);

//         // Perform the request and verify the response
//         mockMvc.perform(put("/api/course-offerings/{id}", 1)
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(asJsonString(courseOfferingDto)))
//                 .andExpect(status().isOk())
//                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                 .andExpect(jsonPath("$.id").value(1))
//                 .andExpect(jsonPath("$.name").value("Updated Course"))
//                 .andExpect(jsonPath("$.courseNumber").value("COURSE303"))
//                 .andExpect(jsonPath("$.notes").value("Updated Notes"));

//         // Verify that courseOfferingRepository.findById() and courseOfferingRepository.save() were called once with the correct data
//         verify(courseOfferingRepository, times(1)).findById(1);
//         verify(courseOfferingRepository, times(1)).save(any(CourseOffering.class));
//         verifyNoMoreInteractions(courseOfferingRepository);
//     }

//     @Test
//     public void testDeleteOneById() throws Exception {
//         // Perform the request and verify the response
//         mockMvc.perform(delete("/api/course-offerings/{id}", 1))
//                 .andExpect(status().isOk());

//         // Verify that courseOfferingRepository.deleteById() was called once with the correct ID
//         verify(courseOfferingRepository, times(1)).deleteById(1);
//         verifyNoMoreInteractions(courseOfferingRepository);
//     }

// }
