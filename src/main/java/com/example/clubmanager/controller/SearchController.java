package com.example.clubmanager.controller;

import com.example.clubmanager.dto.BookingDTO;
import com.example.clubmanager.model.BookingModel;
import com.example.clubmanager.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class SearchController {
    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public List<BookingDTO> searchBookings(@RequestParam(required = false) String memberName,
                                           @RequestParam(required = false) LocalDate startDate,
                                           @RequestParam(required = false) LocalDate endDate) {

        List<BookingModel> bookingModels = searchService.searchBookings(memberName, startDate, endDate);

        return bookingModels.stream().map(this::convertToDTO).toList();

    }
     private BookingDTO convertToDTO(BookingModel bookingModel) {
        return new BookingDTO(
                bookingModel.getClassSession().getClubClass().getName(),
                bookingModel.getMemberName(),
                bookingModel.getParticipationDate()
        );
    }
}
