package com.example.clubmanager.repository;

import com.example.clubmanager.model.ClubClassModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClubClassRepository extends JpaRepository<ClubClassModel, Long> {
    // Find all classes by name
    List<ClubClassModel> findByNameEqualsIgnoreCase(String name);
    ClubClassModel findByNameEqualsIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(String name, LocalDate startDate, LocalDate endDate);
}
