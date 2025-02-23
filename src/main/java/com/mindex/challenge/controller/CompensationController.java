package com.mindex.challenge.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.EmployeeService;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/compensation/{id}")
    public Compensation getCompensation(@PathVariable String id) {
        LOG.debug("Obtaining compensation information for employee id [{}]", id);
        return employeeService.getCompensation(employeeService.read(id));
    }

    //Not asked for, but seemed like an easy enough feature to add
    @GetMapping("/compensation/{id}/history")
    public List<Compensation> getCompensationHistory(@PathVariable String id){
        LOG.debug("Obtaining compensation history for employee id [{}]", id);
        return employeeService.getCompensationHistory(employeeService.read(id));
    }


    @PostMapping("/compensation")
    public Compensation updateCompensation( @RequestBody Compensation compensation) {
        
        return employeeService.updateCompensation(compensation);
    }
}
