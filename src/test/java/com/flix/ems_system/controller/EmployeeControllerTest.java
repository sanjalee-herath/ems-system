package com.flix.ems_system.controller;

import com.flix.ems_system.NoSecurityConfig;
import com.flix.ems_system.dto.EmployeeDTO;
import com.flix.ems_system.service.EmployeeService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(EmployeeController.class)
@SpringBootTest
@AutoConfigureMockMvc
@Import(NoSecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeController employeeController;

    @MockBean
    private EmployeeService employeeService;

    private String generateMockJwtToken() {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        return Jwts.builder()
                .setSubject("user")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour validity
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    @Test
    public void shouldReturnAllEmployees() throws Exception {
        EmployeeDTO employee1 = new EmployeeDTO();
        employee1.setId(1);
        employee1.setFirstName("sanjalee");
        employee1.setLastName("herath");

        EmployeeDTO employee2 = new EmployeeDTO();
        employee2.setId(1);
        employee2.setFirstName("sanjalee");
        employee2.setLastName("herath");

        List<EmployeeDTO> allEmployees = new ArrayList<>();
        allEmployees.add(employee1);
        allEmployees.add(employee2);

        when(employeeService.findAll()).thenReturn(allEmployees);
        String token = generateMockJwtToken();

        mockMvc.perform(get("/employee")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @ParameterizedTest
    @ValueSource(ints={1})
    public void employeeShouldReturnWhenIdIsPassed(int id) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(id);
        employeeDTO.setFirstName("sanjalee");
        employeeDTO.setLastName("herath");

        // Mock the behavior of employeeService
        when(employeeService.findOne(anyInt())).thenReturn(employeeDTO);

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        // Call the controller method
        ResponseEntity<EmployeeDTO> responseEntity = employeeController.getEmployeeById(id);

        // Assert response is not null and has correct body
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(1, Objects.requireNonNull(responseEntity.getBody()).getId());
    }
}
