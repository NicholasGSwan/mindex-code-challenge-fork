package com.mindex.challenge.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(){
        super("No employee found for that Id.");
    }
}
