package com.flix.ems_system.controller;

import com.flix.ems_system.dto.EmployeeDTO;
import com.flix.ems_system.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("employee")
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @GetMapping("hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable int id){
        System.out.println("============= 1");
        System.out.println(id);
        EmployeeDTO employeeDTO =  employeeService.findOne(id);
        System.out.println("=============== 2");
        System.out.println(employeeDTO);
        return ResponseEntity.ok(employeeDTO);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO e){
        EmployeeDTO employee = employeeService.create(e);
        return ResponseEntity.ok(employee);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> listAllEmployee(){
        List<EmployeeDTO> employeeList = employeeService.findAll();
        return ResponseEntity.ok(employeeList);
    }

    @GetMapping("search")
    public ResponseEntity<List<EmployeeDTO>> searchEmployee(@RequestParam String firstName, @RequestParam String lastName){
        List<EmployeeDTO> employeeDTOList = employeeService.search(firstName, lastName);
        return ResponseEntity.ok(employeeDTOList);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@RequestBody EmployeeDTO e){
        EmployeeDTO employeeDTO = employeeService.update(e);
        return ResponseEntity.ok(employeeDTO);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_admin')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable int id){
        employeeService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
