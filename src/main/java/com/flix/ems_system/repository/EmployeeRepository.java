package com.flix.ems_system.repository;

import com.flix.ems_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<Employee> findByFirstNameAndLastName(String firstName, String lastName);
}
