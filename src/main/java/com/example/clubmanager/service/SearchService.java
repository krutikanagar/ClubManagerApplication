package com.example.clubmanager.service;

import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SearchService {

    private final BookingRepository bookingRepository;

    @Autowired
    public SearchService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /*Search for bookings*/
    public List<BookingModel> searchBookings(String memberName, LocalDate startDate, LocalDate endDate) {
        if(memberName != null && startDate != null && endDate != null) {
            return bookingRepository.findByMemberNameEqualsIgnoreCaseAndParticipationDateBetween
                    (memberName, startDate, endDate);
        } else if(memberName != null) {
            return bookingRepository.findByMemberNameEqualsIgnoreCase(memberName);
        } else if(startDate != null && endDate != null) {
            return bookingRepository.findByParticipationDateBetween(startDate, endDate);
        } else {
            return bookingRepository.findAll();
        }
    }

}

