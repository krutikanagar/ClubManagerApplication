package com.example.clubmanager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.BookingDTO;
import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.model.ClassSessionModel;
import com.example.clubmanager.model.ClubClassModel;
import com.example.clubmanager.repository.BookingRepository;
import com.example.clubmanager.repository.ClassSessionRepository;
import com.example.clubmanager.repository.ClubClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class BookingServiceTest {

    private BookingService bookingService;

    @Mock
    private ClubClassRepository clubClassRepository;

    @Mock
    private ClassSessionRepository classSessionRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Autowired
    private Validator validator;

    private ClubClassModel clubClassModel;
    private ClassSessionModel classSessionModel;
    private BookingDTO bookingDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingService = new BookingService(bookingRepository, classSessionRepository, clubClassRepository);

        bookingDTO = new BookingDTO("Pilates", "John Doe", LocalDate.of(2025, 2, 10));

        clubClassModel = new ClubClassModel("Pilates", LocalDate.of(2025, 2, 8), LocalDate.of(2025, 2, 25),
                LocalTime.of(14, 0), 60, 10);
        classSessionModel =
                new ClassSessionModel(clubClassModel, LocalDate.of(2025, 2, 10), LocalTime.of(14, 0), 10);
    }

    @Test
    void testBookClass_Success() {
        // Arrange
        when(clubClassRepository.findByNameEqualsIgnoreCase(anyString())).thenReturn(List.of(clubClassModel));
        when(clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(clubClassModel);
        when(classSessionRepository.findByClubClassAndDate(clubClassModel, bookingDTO.getParticipationDate()))
                .thenReturn(classSessionModel);
        when(bookingRepository.save(any(BookingModel.class))).thenReturn(new BookingModel());

        // Act
        ApiResponse response = bookingService.bookClass(bookingDTO);

        // Assert
        assertNotNull(response);
        assertEquals("success", response.getStatus());
        assertEquals("Member John Doe booked for Pilates class successfully.", response.getMessage());

        // Verify repository interactions
        verify(bookingRepository, times(1)).save(any(BookingModel.class));
        verify(classSessionRepository, times(1)).save(any(ClassSessionModel.class));
    }

    // Test Case 2: Class name is null
    @Test
    void testBookClass_Failure_ClassNameNull() {
        // Arrange
        bookingDTO.setClassName("");
        BindException errors = new BindException(bookingDTO, "bookingDTO");
        validator.validate(bookingDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("class name is required", errors.getFieldError("className").getDefaultMessage());
    }

    // Test Case 3: Member name is null
    @Test
    void testBookClass_Failure_MemberNameNull() {
        // Arrange
        bookingDTO.setMemberName("");
        BindException errors = new BindException(bookingDTO, "bookingDTO");
        validator.validate(bookingDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("member name is required", errors.getFieldError("memberName").getDefaultMessage());
    }

    // Test Case 4: Participation date is null
    @Test
    void testBookClass_Failure_ParticipationDateNull() {
        // Arrange
        bookingDTO.setParticipationDate(null);
        BindException errors = new BindException(bookingDTO, "bookingDTO");
        validator.validate(bookingDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("participation date is required", errors.getFieldError("participationDate").getDefaultMessage());
    }

    // Test Case 5: Participation date is in the past
    @Test
    void testBookClass_Failure_ParticipationDateInThePast() {
        // Arrange
        bookingDTO.setParticipationDate(LocalDate.of(2025, 1, 10));
        BindException errors = new BindException(bookingDTO, "bookingDTO");
        validator.validate(bookingDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("participation date must be in the future", errors.getFieldError("participationDate").getDefaultMessage());
    }

    // Test Case 6: Class not found
    @Test
    void testBookClass_Failure_ClassNotFound() {
        // Arrange
        when(clubClassRepository.findByNameEqualsIgnoreCase(bookingDTO.getClassName())).thenReturn(new ArrayList<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingService.bookClass(bookingDTO));
        assertEquals("Class not found with the given name", exception.getMessage());
    }

    // Test Case 7: Class session not found
    @Test
    void testBookClass_Failure_ClassSessionNotFound() {
        // Arrange
        when(clubClassRepository.findByNameEqualsIgnoreCase(bookingDTO.getClassName())).thenReturn(List.of(clubClassModel));
        when(clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(bookingDTO.getClassName(), bookingDTO.getParticipationDate(), bookingDTO.getParticipationDate()))
                .thenReturn(clubClassModel);
        when(classSessionRepository.findByClubClassAndDate(clubClassModel, bookingDTO.getParticipationDate()))
                .thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingService.bookClass(bookingDTO));
        assertEquals("Class session not found for this date", exception.getMessage());
    }

    // Test Case 8: Class is full
    @Test
    void testBookClass_Failure_ClassIsFull() {

        // Mocking to simulate the class is full
        when(clubClassRepository.findByNameEqualsIgnoreCase(bookingDTO.getClassName())).thenReturn(List.of(clubClassModel));
        when(clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(bookingDTO.getClassName(), bookingDTO.getParticipationDate(), bookingDTO.getParticipationDate()))
                .thenReturn(clubClassModel);
        when(classSessionRepository.findByClubClassAndDate(clubClassModel, bookingDTO.getParticipationDate()))
                .thenReturn(classSessionModel);
        when(classSessionRepository.save(any(ClassSessionModel.class))).thenAnswer(invocation -> {
            classSessionModel.incrementBookingCount();
            return classSessionModel;
        });

        classSessionModel.setBookedCount(60); // Set the session as full

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> bookingService.bookClass(bookingDTO));
        assertEquals("Class is full for this date", exception.getMessage());
    }


}