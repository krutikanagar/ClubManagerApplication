package com.example.clubmanager.controller;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.ClubClassDTO;
import com.example.clubmanager.service.ClubClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/classes")
public class ClubClassController {

    // Constructor-based injection for ClubClassService
    public ClubClassService clubClassService;

    @Autowired
    public ClubClassController(ClubClassService clubClassService) {
        this.clubClassService = clubClassService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createClass(@RequestBody @Valid ClubClassDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse("error", errorMessage));
        }
        try{
            ApiResponse response = clubClassService.createClass(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Return success message
            ApiResponse failureResponse =  new ApiResponse("error", e.getMessage());
            return new ResponseEntity<>(failureResponse, HttpStatus.BAD_REQUEST);
        }

    }
}
