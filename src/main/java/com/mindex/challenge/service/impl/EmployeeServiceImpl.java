package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Reading employee with id [{}]", id);
        Employee employee = employeeRepository.findByEmployeeId(id);
        //triggers controller advice to return a not found message instead of a 500
        if(Objects.isNull(employee)){
            throw new EmployeeNotFoundException();
        }

        return employee;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }

    @Override
    public ReportingStructure getReportingStructure(Employee employee) {
        //check to see if employee has direct reports, if not return 0
        if(employee.getDirectReports() == null || employee.getDirectReports().isEmpty()){
            LOG.debug("No direct reports for employee id [{}]", employee.getEmployeeId());
            return new ReportingStructure(employee.getEmployeeId(), 0L);
        }
        ReportingStructure repStruc = new ReportingStructure();
        repStruc.setEmployee(employee.getEmployeeId());

        //queue for reports id's
        Deque<String> reportsQueue = new ArrayDeque<>();
        //queue up direct reports in order to obtain their direct reports
        for(Employee emp: employee.getDirectReports()){
            LOG.debug("Adding id [{}] to reports queue", emp.getEmployeeId());
            reportsQueue.add(emp.getEmployeeId());
        }
        //using a set to prevent duplicates
        Set<String> empSet = new HashSet<>();

        //iterate through queue until there are no reports left
        while(!reportsQueue.isEmpty()){
            var currEmployeeId = reportsQueue.removeFirst();
            LOG.debug("Getting employee data for id [{}]:", currEmployeeId);
            var currEmployee = employeeRepository.findByEmployeeId(currEmployeeId);
            //checks to make sure an employee was returned, adds their directs to the queue,
            //then adds their employee id to the set
            if(!Objects.isNull(currEmployee)){
                if(currEmployee.getDirectReports()!= null){
                    for(Employee emp: currEmployee.getDirectReports()){
                        reportsQueue.add(emp.getEmployeeId());
                    }
                }

                empSet.add(currEmployeeId);
            } 
        }
        //size of the set should return the number of unique reports beneath them
        repStruc.setNumberOfReports((long) empSet.size());

        return repStruc;      

    }
}
