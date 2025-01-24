package com.example.clubmanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    @NotBlank(message = "class name is required")
    private String className;

    @NotBlank(message = "member name is required")
    private String memberName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "participation date is required")
    @Future(message = "participation date must be in the future")
    private LocalDate participationDate;

}

