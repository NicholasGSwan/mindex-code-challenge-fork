package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.BadCompensationRequestException;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;

    @Autowired
    private EmployeeService employeeService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
    }

    @Test
    public void testCreateReadUpdate() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        // Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();
        Employee createdEmployee = employeeService.create(testEmployee);
        
        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        // Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        Employee readEmployee = employeeService.read(createdEmployee.getEmployeeId());

        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);

        String badEmployee = "1234566789lasdfges";

        RuntimeException runtimeException =  assertThrows(EmployeeNotFoundException.class, ()-> employeeService.read(badEmployee));
        assertEquals("No employee found for that Id." , runtimeException.getMessage());


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee = employeeService.update(readEmployee);
                // restTemplate.exchange(employeeIdUrl,
                //         HttpMethod.PUT,
                //         new HttpEntity<Employee>(readEmployee, headers),
                //         Employee.class,
                //         readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    @Test
    public void testReportingStructure(){
        ReportingStructure reportingStructure = new ReportingStructure();
        Employee emp1 = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");
        Employee emp2 = employeeService.read("b7839309-3348-463b-a7e3-5de1c168beb3");
        reportingStructure = employeeService.getReportingStructure(emp1);
        assertEquals(emp1.getEmployeeId(), reportingStructure.getEmployee());
        assertEquals(4L, reportingStructure.getNumberOfReports());

        reportingStructure = employeeService.getReportingStructure(emp2);
        assertEquals(emp2.getEmployeeId(), reportingStructure.getEmployee());
        assertEquals(0L, reportingStructure.getNumberOfReports());
    }

    @Test
    public void testCompensation(){
        Employee emp1 = employeeService.read("16a596ae-edd3-4847-99fe-c4518e82c86f");
        List<Compensation> compHistory = new ArrayList<>();

        RuntimeException runtimeException = assertThrows(CompensationNotFoundException.class,() -> employeeService.getCompensation(emp1));
        assertEquals("No Compensation found for that employee Id.", runtimeException.getMessage());

        Compensation compensation = new Compensation();

        runtimeException = assertThrows(BadCompensationRequestException.class, () -> employeeService.updateCompensation(compensation));
        assertEquals("No Salary specified in request", runtimeException.getMessage());

        compensation.setSalary(new BigDecimal(100000));

        runtimeException = assertThrows(BadCompensationRequestException.class, () -> employeeService.updateCompensation(compensation));
        assertEquals("Request body does not contain an Employee id", runtimeException.getMessage());


        compensation.setEmployee(emp1.getEmployeeId());
        
        var comp2 = employeeService.updateCompensation(compensation);
        assertCompensationEquivalence(compensation, comp2);
        assertNotNull(comp2.getEffectiveDate());

        compensation.setSalary(new BigDecimal(120000));
        
        var comp3 = employeeService.updateCompensation(compensation);
        assertCompensationEquivalence(compensation, comp3);
        assertNotNull(comp2.getEffectiveDate());
        
        comp3 = employeeService.getCompensation(emp1);

        //assert service is returning latest compensation
        assertNotEquals(comp2.getSalary(), comp3.getSalary());

        compensation.setEffectiveDate(LocalDateTime.now().plusDays(5L));
        compensation.setSalary(new BigDecimal(200000000));
        var comp4 = employeeService.updateCompensation(compensation);

        //assert get compensation returns current compensation and not future dated compensation
        assertEquals(comp3.getSalary(), employeeService.getCompensation(emp1).getSalary());
        assertNotEquals(comp4.getSalary(), employeeService.getCompensation(emp1).getSalary());

        //assert compensation history is not empty
        compHistory = employeeService.getCompensationHistory(emp1);
        assertNotEquals(0, compHistory.size());

    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual){
        assertEquals(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
}
