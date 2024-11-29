package com.flix.ems_system.service;

import com.flix.ems_system.dto.EmployeeDTO;
import com.flix.ems_system.entity.Employee;
import com.flix.ems_system.event.EmployeeEvent;
import com.flix.ems_system.exception.EmployeeNotFoundException;
import com.flix.ems_system.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    private EmployeeService employeeService;
    private ModelMapper modelMapper = new ModelMapper();

    @BeforeEach
    public void setUp() {
        employeeService = new EmployeeService(employeeRepository, modelMapper, kafkaTemplate, taskService);
    }

    @Test
    public void employeeListShouldReturn() {
        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("sanjalee");
        employee1.setLastName("herath");

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setFirstName("lakmal");
        employee2.setLastName("dharmasena");

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);

        when(employeeRepository.findAll()).thenReturn(employeeList);
        List<EmployeeDTO> expectedEmployeeDTOList = employeeService.findAll();

        assertEquals(1, expectedEmployeeDTOList.get(0).getId());
        assertEquals(2, expectedEmployeeDTOList.get(1).getId());
    }

    @Test
    public void employeeShouldReturnWhenIdPassed() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("sanjalee");
        employee.setLastName("herath");

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        EmployeeDTO expectedEmployeeDTO = employeeService.findOne(1);

        assertEquals(1, expectedEmployeeDTO.getId());
        assertEquals("sanjalee", expectedEmployeeDTO.getFirstName());
        assertEquals("herath", expectedEmployeeDTO.getLastName());
    }

    @Test
    public void exceptionShouldThrowWhenEmployeeNotFound() {
        Exception exception = assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.findOne(1);
        });

        String expectedMessage = "Employee not found with id 1";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void employeeShouldReturnWhenEmployeeSaved() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setFirstName("sanjalee");
        employee.setLastName("herath");

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(1);
        employeeDTO.setFirstName("sanjalee");
        employeeDTO.setLastName("herath");

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        EmployeeDTO expectedEmployeeDTO = employeeService.create(employeeDTO);

        assertNotNull(expectedEmployeeDTO);
        assertEquals(employeeDTO.getFullName(), expectedEmployeeDTO.getFullName());
        assertEquals(employeeDTO.getId(), expectedEmployeeDTO.getId());
        assertEquals(employeeDTO.getDateOfBirth(), expectedEmployeeDTO.getDateOfBirth());
    }

    @Test
    public void updatedEmployeeShouldReturnWhenEmployeeUpdated() {

    }

    @Test
    public void employeeListShouldReturnWhenSearchedWithMatchingEmployees() {
        String firstName = "sanjalee";
        String lastName = "herath";

        Employee employee1 = new Employee();
        employee1.setId(1);
        employee1.setFirstName("sanjalee");
        employee1.setLastName("herath");

        Employee employee2 = new Employee();
        employee2.setId(2);
        employee2.setFirstName("sanjalee");
        employee2.setLastName("herath");

        List<Employee> employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);


        when(employeeRepository.findByFirstNameAndLastName(firstName, lastName)).thenReturn(employeeList);
        List<EmployeeDTO> expectedEmployeeDTOList = employeeService.search(firstName, lastName);

        assertEquals("sanjalee", expectedEmployeeDTOList.get(0).getFirstName());
        assertEquals("herath", expectedEmployeeDTOList.get(0).getLastName());
        assertEquals("sanjalee", expectedEmployeeDTOList.get(1).getFirstName());
        assertEquals("herath", expectedEmployeeDTOList.get(1).getLastName());
    }
}
