package com.example.clubmanager.service;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.ClubClassDTO;
import com.example.clubmanager.model.ClubClassModel;
import com.example.clubmanager.repository.ClubClassRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ClubClassServiceTest {

    @Mock
    private ClubClassRepository clubClassRepository;

    @Autowired
    private Validator validator;

    private ClubClassService clubClassService;

    private ClubClassDTO validClubClassDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clubClassService = new ClubClassService(clubClassRepository);

        // Prepare a valid ClubClassDTO
        validClubClassDTO = new ClubClassDTO("Pilates",
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 10),
                LocalTime.of(10,0),60, 20);
    }

    @Test
    void testCreateClass_Success() {
        // Arrange: Mock the repository to return null for the class existence check
        when(clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(null);  // Simulate no existing class for the given dates

        // Act: Call the createClass method
        ApiResponse response = clubClassService.createClass(validClubClassDTO);

        // Assert: Verify the response
        assertEquals("success", response.getStatus());
        assertEquals("Class Pilates created successfully.", response.getMessage());

        // Verify the repository interaction
        verify(clubClassRepository, times(1)).save(any(ClubClassModel.class));
    }


    // Test Case: Class name is null
    @Test
    void testCreateClass_Failure_ClassNameNull() {
        // Arrange
        validClubClassDTO.setName("");
        BindException errors = new BindException(validClubClassDTO, "ClubClassDTO");
        validator.validate(validClubClassDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("class name is required", errors.getFieldError("name").getDefaultMessage());
    }

    @Test
    void testBookClass_Failure_StartDateNull() {
        // Arrange
        validClubClassDTO.setStartDate(null);
        BindException errors = new BindException(validClubClassDTO, "bookingDTO");
        validator.validate(validClubClassDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("start date is required", errors.getFieldError("startDate").getDefaultMessage());
    }

    // Test Case: Start date is in the past
    @Test
    void testBookClass_Failure_StartDateInThePast() {
        // Arrange
        validClubClassDTO.setStartDate(LocalDate.of(2025, 1, 10));
        BindException errors = new BindException(validClubClassDTO, "bookingDTO");
        validator.validate(validClubClassDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("start date must be in the future", errors.getFieldError("startDate").getDefaultMessage());
    }

    @Test
    void testCreateClass_Failure_CapacityNull() {
        // Arrange
        validClubClassDTO.setCapacity(0);
        BindException errors = new BindException(validClubClassDTO, "ClubClassDTO");
        validator.validate(validClubClassDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("Min capacity required is 1", errors.getFieldError("capacity").getDefaultMessage());
    }

    @Test
    void testCreateClass_EndDateInThePast() {
        // Arrange
        validClubClassDTO.setEndDate(LocalDate.of(2025, 1, 10));
        BindException errors = new BindException(validClubClassDTO, "bookingDTO");
        validator.validate(validClubClassDTO, errors);

        assertEquals(1, errors.getErrorCount());
        assertEquals("end date must be in the future", errors.getFieldError("endDate").getDefaultMessage());

    }

    @Test
    void testCreateClass_StartDateAfterEndDate() {
        // Arrange: Modify the DTO to have a start date after the end date
        validClubClassDTO.setStartDate(LocalDate.of(2025, 2, 15));

        // Act: Call the createClass method
        ApiResponse response = clubClassService.createClass(validClubClassDTO);

        // Assert: Verify the response
        assertEquals("error", response.getStatus());
        assertEquals("Start date must be before end date.", response.getMessage());

        // Verify no repository interaction, since we short-circuited the logic
        verify(clubClassRepository, times(0)).save(any(ClubClassModel.class));
    }

    @Test
    void testCreateClass_ClassAlreadyExists() {
        // Arrange: Mock the repository to return an existing class
        when(clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new ClubClassModel());

        // Act: Call the createClass method
        ApiResponse response = clubClassService.createClass(validClubClassDTO);

        // Assert: Verify the response
        assertEquals("error", response.getStatus());
        assertEquals("Class already exists for the given period", response.getMessage());

        // Verify no repository interaction, since we short-circuited the logic
        verify(clubClassRepository, times(0)).save(any(ClubClassModel.class));
    }

}
