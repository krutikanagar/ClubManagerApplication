package com.example.clubmanager.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class BookingModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "class_session_id")
    private ClassSessionModel classSession;

    private String memberName;
    private LocalDate participationDate;

    public String getMemberName(){
        return memberName;
    }
    public void setMemberName(String memberName){
        this.memberName = memberName;
    }

    public LocalDate getParticipationDate(){
        return participationDate;
    }

    public void setParticipationDate(LocalDate participationDate){
        this.participationDate = participationDate;
    }

    public void setClassSession(ClassSessionModel classSession) {
        this.classSession = classSession;
    }

    public ClassSessionModel getClassSession() {
        return classSession;
    }
}
