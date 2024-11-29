package com.flix.ems_system.service;

import com.flix.ems_system.dto.EmployeeDTO;
import com.flix.ems_system.entity.Employee;
import com.flix.ems_system.event.EmployeeEvent;
import com.flix.ems_system.exception.EmployeeNotFoundException;
import com.flix.ems_system.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;
    private final TaskService taskService;

    private final String EMPLOYEE_CREATED_EVENT = "EMPLOYEE_CREATED_EVENT";
    private final String EMPLOYEE_UPDATED_EVENT = "EMPLOYEE_UPDATED_EVENT";
    private final String EMPLOYEE_TOPIC = "employee-topic";

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, ModelMapper modelMapper,
                           KafkaTemplate<String, EmployeeEvent> kafkaTemplate, TaskService taskService){
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.taskService = taskService;
    }

    public List<EmployeeDTO> findAll() {
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        List<Employee> employeeList = employeeRepository.findAll();

        for(Employee employee: employeeList){
            EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);
            employeeDTOList.add(employeeDTO);
        }
        return employeeDTOList;
    }

    public EmployeeDTO findOne(int id){
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if(optionalEmployee.isPresent()){
            return modelMapper.map(optionalEmployee.get(), EmployeeDTO.class);
        }
        else{
            throw new EmployeeNotFoundException("Employee not found with id " + id);
        }
    }

    public EmployeeDTO create(EmployeeDTO e){
        Employee employee = modelMapper.map(e, Employee.class);
        employee.getTaskList().forEach(t -> t.setEmployee(employee));
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeDTO employeeDTO = modelMapper.map(savedEmployee, EmployeeDTO.class);
        employeeDTO.getTaskList().forEach(t -> taskService.publishTaskEvent("TASK_CREATED_EVENT", t));
        publishEmployeeEvent(EMPLOYEE_CREATED_EVENT, employeeDTO);

        return employeeDTO;
    }

    public List<EmployeeDTO> search(String firstName, String lastName){
        List<Employee> employeeList = employeeRepository.findByFirstNameAndLastName(firstName, lastName);
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for(Employee employee: employeeList){
            EmployeeDTO employeeDTO = modelMapper.map(employee, EmployeeDTO.class);
            employeeDTOList.add(employeeDTO);
        }
        return employeeDTOList;
    }

    public EmployeeDTO update(EmployeeDTO e){
        Employee employee = modelMapper.map(e, Employee.class);
        employee.getTaskList().forEach(t -> t.setEmployee(employee));
        Employee savedEmployee = employeeRepository.save(employee);

        EmployeeDTO employeeDTO = modelMapper.map(savedEmployee, EmployeeDTO.class);
        employeeDTO.getTaskList().forEach(t -> taskService.publishTaskEvent("TASK_UPDATED_EVENT", t));
        publishEmployeeEvent(EMPLOYEE_UPDATED_EVENT, employeeDTO);

        return employeeDTO;
    }

    public void delete(int id){
        employeeRepository.deleteById(id);
    }

    private void publishEmployeeEvent(String eventType, EmployeeDTO employeeDTO) {
        EmployeeEvent employeeEvent = new EmployeeEvent(eventType, employeeDTO.getId(),
                employeeDTO.getFullName(), employeeDTO.getDateOfBirth());

        kafkaTemplate.send(EMPLOYEE_TOPIC, employeeEvent);
    }
}
