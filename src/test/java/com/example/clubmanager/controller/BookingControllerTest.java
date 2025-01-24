package com.example.clubmanager.controller;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.BookingDTO;
import com.example.clubmanager.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private BookingDTO validBookingDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        // Set up a valid BookingDTO object
        validBookingDTO = new BookingDTO("Pilates", "John Doe", LocalDate.of(2025,2,10));
    }

    @Test
    void testCreateBooking_Success() throws Exception {
        // Arrange: Mock the service call to return a success response
        ApiResponse mockResponse = new ApiResponse("success", "Member John Doe booked for Pilates class successfully.");
        when(bookingService.bookClass(any(BookingDTO.class))).thenReturn(mockResponse);

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/bookings/create")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(validBookingDTO)))
                .andDo(print())
                .andExpect(status().isCreated()) // Expect HTTP 201
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Member John Doe booked for Pilates class successfully."));

        // Verify the service interaction
        verify(bookingService, times(1)).bookClass(any(BookingDTO.class));
    }

    @Test
    void testCreateBooking_Failure_InvalidMemberName() throws Exception {

        validBookingDTO.setMemberName("");

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/bookings/create")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .content(objectMapper.writeValueAsString(validBookingDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())  // Expect HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("member name is required"));

        // Verify the service interaction
        verify(bookingService, times(0)).bookClass(any(BookingDTO.class));
    }

    @Test
    void testCreateBooking_Failure_InvalidClassName() throws Exception {

        validBookingDTO.setClassName(null);

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/bookings/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validBookingDTO)))
                .andExpect(status().isBadRequest())  // Expect HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("class name is required"));

        // Verify the service interaction
        verify(bookingService, times(0)).bookClass(any(BookingDTO.class));
    }

    @Test
    void testCreateBooking_Failure_InvalidParticipationDate() throws Exception {

        validBookingDTO.setParticipationDate(null);

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/bookings/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validBookingDTO)))
                .andExpect(status().isBadRequest())  // Expect HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("participation date is required"));

        // Verify the service interaction
        verify(bookingService, times(0)).bookClass(any(BookingDTO.class));
    }

    @Test
    void testCreateBooking_Failure_ParticipationDateInThePast() throws Exception {
        validBookingDTO.setParticipationDate(LocalDate.now().minusDays(1));

        // Act & Assert: Perform POST request and verify the response
        mockMvc.perform(post("/api/bookings/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validBookingDTO)))
                .andExpect(status().isBadRequest())  // Expect HTTP 400
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("participation date must be in the future"));

        // Verify the service interaction
        verify(bookingService, times(0)).bookClass(any(BookingDTO.class));
    }
}

