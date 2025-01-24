package com.example.clubmanager.repository;

import com.example.clubmanager.model.BookingModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingModel, Long> {
    List<BookingModel> findByMemberNameEqualsIgnoreCase(String memberName);
    List<BookingModel> findByParticipationDateBetween(LocalDate startDate, LocalDate endDate);
    List<BookingModel> findByMemberNameEqualsIgnoreCaseAndParticipationDateBetween(String memberName, LocalDate participationDate1, LocalDate participationDate2);

}

