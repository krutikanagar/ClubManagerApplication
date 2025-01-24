package com.example.clubmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClubClassDTO {

    @NotBlank(message = "class name is required")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "start date must be in the future")
    @NotNull(message = "start date is required")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Future(message = "end date must be in the future")
    @NotNull(message = "end date is required")
    private LocalDate endDate;

    @NotNull(message = "start time is required")
    private LocalTime startTime;

    @NotNull @Min(value = 10, message = "minimum duration required is 10 minutes")
    private int duration;

    @NotNull @Min(value = 1, message = "Min capacity required is 1")
    private int capacity;

}
