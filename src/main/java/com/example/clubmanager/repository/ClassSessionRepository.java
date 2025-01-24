package com.example.clubmanager.repository;

import com.example.clubmanager.model.ClassSessionModel;
import com.example.clubmanager.model.ClubClassModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSessionModel, Long> {

    ClassSessionModel findByClubClassAndDate(ClubClassModel clubClass, LocalDate date);

}
