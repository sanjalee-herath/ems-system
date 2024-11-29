package com.flix.ems_system.event;

import java.time.LocalDate;

public class EmployeeEvent {
    private String eventType;
    private int id;
    private String fullName;
    private LocalDate dateOfBirth;

    public EmployeeEvent(String eventType, int id, String fullName, LocalDate dateOfBirth) {
        this.eventType = eventType;
        this.id = id;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
