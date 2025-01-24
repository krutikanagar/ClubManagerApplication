package com.example.clubmanager.controller;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.ClubClassDTO;
import com.example.clubmanager.service.ClubClassService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class ClubClassControllerTest {

    @InjectMocks
    private ClubClassController clubClassController;

    @Mock
    private ClubClassService clubClassService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    private ClubClassDTO classDTO;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc and ObjectMapper before each test
        mockMvc = MockMvcBuilders.standaloneSetup(clubClassController).build();
        classDTO = new ClubClassDTO("Pilates",
                LocalDate.of(2025,12,1),
                LocalDate.of(2025,12,20),
                LocalTime.of(14,0), 60,10);
    }

    @Test
    void testCreateClass_Success() throws Exception {
        // Arrange: Prepare a valid ClubClassDTO and mock the service call
        ApiResponse successResponse = new ApiResponse("success", "Class Pilates created successfully.");

        when(clubClassService.createClass(any(ClubClassDTO.class))).thenReturn(successResponse);

        // Act & Assert: Perform POST request and check the response
        mockMvc.perform(post("/api/classes/create")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(classDTO)))
                .andDo(print())
                .andExpect(status().isCreated())  // HTTP 201 for successful creation
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Class Pilates created successfully."));
    }

    @Test
    void testCreateClass_Failure_InvalidStartDate() throws Exception {
        // Arrange: Create a ClubClassDTO with an invalid start date (before today)
        classDTO.setStartDate(LocalDate.of(2025,1,1));
        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/classes/create")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(classDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())  // Expect HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("start date must be in the future"));
    }

    @Test
    void testCreateClass_Failure_InvalidClassName() throws Exception {
        // Arrange: Prepare an invalid ClubClassDTO (empty class name)
        classDTO.setName("");

        // Act & Assert: Mock the service to throw IllegalArgumentException
        mockMvc.perform(post("/api/classes/create")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(classDTO)))
                .andExpect(status().isBadRequest())  // HTTP 400 for bad request
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("class name is required"));
    }

}
