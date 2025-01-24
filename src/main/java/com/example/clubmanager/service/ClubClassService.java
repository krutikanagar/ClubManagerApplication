package com.example.clubmanager.service;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.ClubClassDTO;
import com.example.clubmanager.model.ClassSessionModel;
import com.example.clubmanager.model.ClubClassModel;
import com.example.clubmanager.repository.ClubClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClubClassService {

    private final ClubClassRepository clubClassRepository;
    @Autowired
    public ClubClassService(ClubClassRepository clubClassRepository) {
        this.clubClassRepository = clubClassRepository;
    }

    public ApiResponse createClass(ClubClassDTO request) {

        // Validation for start date being in the future
        if (!request.getStartDate().isBefore(request.getEndDate())) {
            return new ApiResponse("error", "Start date must be before end date.");
        }

        // Check if class is already created with overlapping start and end date
        ClubClassModel clubClassModel =
                clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual
                        (request.getName(), request.getEndDate(), request.getStartDate());

        if(clubClassModel != null) {
            return new ApiResponse("error", "Class already exists for the given period");
        }

        // Create the club class
        ClubClassModel clubClass = new ClubClassModel(
                request.getName(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStartTime(),
                request.getDuration(),
                request.getCapacity()
        );

        // Generate the class sessions for each day in the date range
        List<ClassSessionModel> sessions = new ArrayList<>();
        LocalDate startDate = request.getStartDate();
        while (!startDate.isAfter(request.getEndDate())) {
            ClassSessionModel session = new ClassSessionModel(clubClass, startDate, request.getStartTime(), request.getCapacity());
            sessions.add(session);
            startDate = startDate.plusDays(1);
        }

        // Set the sessions to the club class and save it
        clubClass.setSessions(sessions);
        clubClassRepository.save(clubClass);

        // Return success message
        return new ApiResponse("success", "Class " + request.getName() + " created successfully.");
    }
}
