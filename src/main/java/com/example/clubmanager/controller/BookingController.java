package com.example.clubmanager.controller;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.BookingDTO;
import com.example.clubmanager.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookingDTO bookingDTO,  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse("error", errorMessage));
        }
        try {
            ApiResponse response = bookingService.bookClass(bookingDTO);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            ApiResponse failureResponse = new ApiResponse("error", e.getMessage());
            return new ResponseEntity<>(failureResponse, HttpStatus.BAD_REQUEST);
        }
    }
}

