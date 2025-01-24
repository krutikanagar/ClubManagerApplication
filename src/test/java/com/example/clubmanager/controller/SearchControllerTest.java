package com.example.clubmanager.controller;

import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.model.ClassSessionModel;
import com.example.clubmanager.model.ClubClassModel;
import com.example.clubmanager.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;

    private BookingModel bookingModel;

    @BeforeEach
    void setUp() {
        // Setup mock data
        ClubClassModel clubClassModel = new ClubClassModel();
        clubClassModel.setName("Pilates");
        clubClassModel.setCapacity(20);
        clubClassModel.setStartDate(LocalDate.of(2025, 2, 1));
        clubClassModel.setEndDate(LocalDate.of(2025, 2, 20));

        ClassSessionModel classSessionModel = new ClassSessionModel();
        classSessionModel.setClubClass(clubClassModel);
        classSessionModel.setDate(LocalDate.of(2025, 2, 10));
        classSessionModel.setStartTime(LocalTime.of(10,0));
        classSessionModel.setCapacity(20);

        bookingModel = new BookingModel();
        bookingModel.setMemberName("John Doe");
        bookingModel.setParticipationDate(LocalDate.of(2025, 2, 10));
        bookingModel.setClassSession(classSessionModel);
        // Initialize MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
    }

    @Test
    void testSearchBookings_ByMemberName() throws Exception {
        // Given
        List<BookingModel> bookings = List.of(bookingModel);
        when(searchService.searchBookings("John Doe", null, null)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                        .param("memberName", "John Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value("John Doe"))
                .andExpect(jsonPath("$[0].className").value("Pilates"))  // assuming Pilates class for the test data
                .andExpect(jsonPath("$[0].participationDate").value("2025-02-10"));

        verify(searchService, times(1)).searchBookings("John Doe", null, null);
    }

    @Test
    void testSearchBookings_ByDateRange() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 20);
        List<BookingModel> bookings = List.of(bookingModel);
        when(searchService.searchBookings(null, startDate, endDate)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-02-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value("John Doe"))
                .andExpect(jsonPath("$[0].className").value("Pilates"))
                .andExpect(jsonPath("$[0].participationDate").value("2025-02-10"));

        verify(searchService, times(1)).searchBookings(null, startDate, endDate);
    }

    @Test
    void testSearchBookings_ByMemberNameAndDateRange() throws Exception {
        // Given
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 20);
        List<BookingModel> bookings = List.of(bookingModel);
        when(searchService.searchBookings("John Doe", startDate, endDate)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                        .param("memberName", "John Doe")
                        .param("startDate", "2025-02-01")
                        .param("endDate", "2025-02-20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value("John Doe"))
                .andExpect(jsonPath("$[0].className").value("Pilates"))
                .andExpect(jsonPath("$[0].participationDate").value("2025-02-10"));

        verify(searchService, times(1)).searchBookings("John Doe", startDate, endDate);
    }

    @Test
    void testSearchBookings_NoCriteria() throws Exception {
        // Given
        List<BookingModel> bookings = List.of(bookingModel);
        when(searchService.searchBookings(null, null, null)).thenReturn(bookings);

        // When & Then
        mockMvc.perform(get("/api/bookings/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value("John Doe"))
                .andExpect(jsonPath("$[0].className").value("Pilates"))
                .andExpect(jsonPath("$[0].participationDate").value("2025-02-10"));

        verify(searchService, times(1)).searchBookings(null, null, null);
    }

    @Test
    void testSearchBookings_WhenNoResultsFound() throws Exception {
        // Given
        when(searchService.searchBookings("Non Existent", null, null)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/bookings/search")
                        .param("memberName", "Non Existent"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(searchService, times(1)).searchBookings("Non Existent", null, null);
    }
}