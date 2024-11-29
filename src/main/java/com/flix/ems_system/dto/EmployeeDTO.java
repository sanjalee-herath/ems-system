package com.flix.ems_system.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class EmployeeDTO {
    private int id;
    private String firstName;
    private String lastName;
    private double salary;
    private LocalDate dateOfBirth;
    private List<TaskDTO> taskList = new ArrayList<>();

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
