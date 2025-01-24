package com.example.clubmanager.service;

import com.example.clubmanager.utilities.ApiResponse;
import com.example.clubmanager.dto.BookingDTO;
import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.model.ClassSessionModel;
import com.example.clubmanager.model.ClubClassModel;
import com.example.clubmanager.repository.BookingRepository;
import com.example.clubmanager.repository.ClassSessionRepository;
import com.example.clubmanager.repository.ClubClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ClassSessionRepository classSessionRepository;
    private final ClubClassRepository clubClassRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ClassSessionRepository classSessionRepository, ClubClassRepository clubClassRepository) {
        this.bookingRepository = bookingRepository;
        this.classSessionRepository = classSessionRepository;
        this.clubClassRepository = clubClassRepository;
    }

    /*Book a class*/
    public ApiResponse bookClass(BookingDTO bookingDTO) {

        // Find the class by name (assume class names are not unique)
        List<ClubClassModel> clubClasses = clubClassRepository.findByNameEqualsIgnoreCase(bookingDTO.getClassName());
        if (clubClasses.isEmpty()){
            throw new IllegalArgumentException("Class not found with the given name");
        }

        //Find class  by name and participation date between start and end date of the class
        ClubClassModel clubClass =
                clubClassRepository.findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual
                        (bookingDTO.getClassName(), bookingDTO.getParticipationDate(), bookingDTO.getParticipationDate());

        if (clubClass == null) {
            throw new IllegalArgumentException("Class not found for the given participation date");
        }

        // Find class session for the given date
        ClassSessionModel classSession = classSessionRepository
                .findByClubClassAndDate(clubClass, bookingDTO.getParticipationDate());

        if (classSession==null) {
            throw new IllegalArgumentException("Class session not found for this date");
        }

        // Check if the session has available capacity
        if (classSession.getBookedCount() >= clubClass.getCapacity()) {
            throw new IllegalArgumentException("Class is full for this date");
        }

        // Create the booking
        BookingModel booking = new BookingModel();
        booking.setMemberName(bookingDTO.getMemberName());
        booking.setParticipationDate(bookingDTO.getParticipationDate());
        booking.setClassSession(classSession);

        // Save the booking
        bookingRepository.save(booking);

        // Increment the booking count for the class session
        classSession.incrementBookingCount();
        classSessionRepository.save(classSession);

        // Return success message
        return new ApiResponse("success", "Member " + bookingDTO.getMemberName() + " booked for "+bookingDTO.getClassName() + " class successfully.");
    }
}

