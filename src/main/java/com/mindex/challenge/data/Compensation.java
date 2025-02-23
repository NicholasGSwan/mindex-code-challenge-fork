package com.mindex.challenge.data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compensation {

    private String employee;
    private BigDecimal salary;
    private LocalDateTime effectiveDate;

    public String getEmployee(){
        return this.employee;
    }

    public void setEmployee(String employee){
        this.employee = employee;
    }

    public BigDecimal getSalary(){
        return this.salary;
    }

    public void setSalary(BigDecimal salary){
        this.salary = salary;
    }

    public LocalDateTime getEffectiveDate(){
        return this.effectiveDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate){
        this.effectiveDate = effectiveDate;
    }
}
