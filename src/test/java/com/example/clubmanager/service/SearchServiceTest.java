package com.example.clubmanager.service;

import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SearchServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    private SearchService searchService;

    private BookingModel booking;

    @BeforeEach
    void setUp() {
        // Setup mock data
        searchService = new SearchService(bookingRepository);
        booking = new BookingModel();
        booking.setMemberName("John Doe");
        booking.setParticipationDate(LocalDate.of(2025, 2, 10));
    }

    @Test
    void testSearchBookings_ByMemberName() {
        // Given
        when(bookingRepository.findByMemberNameEqualsIgnoreCase("John Doe")).thenReturn(List.of(booking));

        // When
        List<BookingModel> result = searchService.searchBookings("John Doe", null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getMemberName());
        verify(bookingRepository, times(1)).findByMemberNameEqualsIgnoreCase("John Doe");
    }

    @Test
    void testSearchBookings_ByDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 20);
        when(bookingRepository.findByParticipationDateBetween(startDate, endDate)).thenReturn(List.of(booking));

        // When
        List<BookingModel> result = searchService.searchBookings(null, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getMemberName());
        verify(bookingRepository, times(1)).findByParticipationDateBetween(startDate, endDate);
    }

    @Test
    void testSearchBookings_ByMemberNameAndDateRange() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 2, 1);
        LocalDate endDate = LocalDate.of(2025, 2, 20);
        when(bookingRepository.findByMemberNameEqualsIgnoreCaseAndParticipationDateBetween("John Doe", startDate, endDate))
                .thenReturn(List.of(booking));

        // When
        List<BookingModel> result = searchService.searchBookings("John Doe", startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getMemberName());
        verify(bookingRepository, times(1)).findByMemberNameEqualsIgnoreCaseAndParticipationDateBetween("John Doe", startDate, endDate);
    }

    @Test
    void testSearchBookings_WithoutCriteria() {
        // Given
        when(bookingRepository.findAll()).thenReturn(List.of(booking));

        // When
        List<BookingModel> result = searchService.searchBookings(null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getMemberName());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void testSearchBookings_WhenNoResultsFound() {
        // Given
        when(bookingRepository.findByMemberNameEqualsIgnoreCase("Non Existent")).thenReturn(List.of());

        // When
        List<BookingModel> result = searchService.searchBookings("Non Existent", null, null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookingRepository, times(1)).findByMemberNameEqualsIgnoreCase("Non Existent");
    }

    @Test
    void testSearchBookings_WithNullResults() {
        // Given
        when(bookingRepository.findByMemberNameEqualsIgnoreCase("John Doe")).thenReturn(null);

        // When
        List<BookingModel> result = searchService.searchBookings("John Doe", null, null);

        // Then
        assertNull(result);
        verify(bookingRepository, times(1)).findByMemberNameEqualsIgnoreCase("John Doe");
    }
}
