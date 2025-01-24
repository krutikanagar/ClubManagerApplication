package com.example.clubmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class ClassSessionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_id")
    private ClubClassModel clubClass;

    private LocalDate date;

    private LocalTime startTime;

    private int capacity;

    private int bookedCount; // Tracks the number of bookings for this session.

    // Constructors, getters, setters
    public ClassSessionModel() {}

    public ClassSessionModel(ClubClassModel clubClass, LocalDate date, LocalTime startTime, int capacity) {
        this.clubClass = clubClass;
        this.date = date;
        this.startTime = startTime;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public ClubClassModel getClubClass() {
        return clubClass;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setClubClass(ClubClassModel clubClass) {
        this.clubClass = clubClass;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setBookedCount(int bookedCount) {
        this.bookedCount = bookedCount;
    }

    public int getBookedCount() {
        return this.bookedCount;
    }

    public void incrementBookingCount() {
        this.bookedCount++;
    }

    @Override
    public String toString() {
        return "Class Session Model {" +
                "class name='" + clubClass.getName() + '\'' +
                ", date=" + date +
                ", startTime='" + startTime + '\'' +
                ", capacity=" + capacity +
                ", bookedCount=" + bookedCount +
                '}';
    }
}
