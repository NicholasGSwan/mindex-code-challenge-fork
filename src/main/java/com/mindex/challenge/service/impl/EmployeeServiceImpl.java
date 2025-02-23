package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.exception.BadCompensationRequestException;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CompensationRepository compensationRepository;

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

//REPORTING STRUCTURE

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

    //came up with a recursive solution for fun. This breaks under two conditions (that I can think of): if there are duplicate reports, and if there are non-valid employee ids in the db
    @Override
    public ReportingStructure getReportingStructureRecursion(Employee employee){
        ReportingStructure repStruc = new ReportingStructure(employee.getEmployeeId(), 0L);
        //check to see if employee has direct reports, if not return 0
        if(employee.getDirectReports() == null || employee.getDirectReports().isEmpty()){
            LOG.debug("No direct reports for employee id [{}]", employee.getEmployeeId());
            return repStruc;
        }

        for(Employee emp: employee.getDirectReports()){
            emp = read(emp.getEmployeeId());
            if(Objects.nonNull(repStruc)) repStruc.add(getReportingStructureRecursion(emp));
        }

        return repStruc;
    }


//COMPENSATION


    @Override
    public Compensation getCompensation(Employee employee){
        List<Compensation> compList = getCompensationHistory(employee);
        int i = 0;
        //will get latest without getting future dated
        while(compList.get(i).getEffectiveDate().isAfter(LocalDateTime.now())){
            i++;
        }

        return compList.get(i);
    }

    @Override
    public List<Compensation> getCompensationHistory(Employee employee){
        List<Compensation> compList = compensationRepository.findByEmployeeOrderByEffectiveDateDesc(employee.getEmployeeId());
        if(Objects.isNull(compList) || compList.isEmpty()){
            throw new CompensationNotFoundException();
        }
        return compList;
    }

    @Override
    public Compensation updateCompensation(Compensation compensation){
        if(Objects.isNull(compensation.getSalary())){
            throw new BadCompensationRequestException("No Salary specified in request");
        }else if(!StringUtils.hasLength(compensation.getEmployee())){
            throw new BadCompensationRequestException("Request body does not contain an Employee id");
        }
        //checks to see if valid employee
        Employee employee = read(compensation.getEmployee());
        LOG.debug("Updating Compensation for employee id: [{}], name {}", employee.getEmployeeId(), employee.getFirstName() + " " + employee.getLastName());

        //I figured if there was no date, we could just set the effective date to now
        if(Objects.isNull(compensation.getEffectiveDate())){
            compensation.setEffectiveDate(LocalDateTime.now());
        }

        return compensationRepository.save(compensation);
    }
}
