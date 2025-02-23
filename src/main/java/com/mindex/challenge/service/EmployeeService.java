package com.mindex.challenge.service;

import java.util.List;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Employee read(String id);
    Employee update(Employee employee);
    ReportingStructure getReportingStructure(Employee employee);
    ReportingStructure getReportingStructureRecursion(Employee employee);
    Compensation getCompensation(Employee employee);
    List<Compensation> getCompensationHistory(Employee employee);
    Compensation updateCompensation(Compensation compensation);
}
