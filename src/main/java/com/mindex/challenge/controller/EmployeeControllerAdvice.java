package com.mindex.challenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mindex.challenge.exception.EmployeeNotFoundException;

@RestControllerAdvice
public class EmployeeControllerAdvice {


    @ExceptionHandler(value = EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String employeeNotFoundHandler(RuntimeException ex){
        return ex.getMessage();
    }

}
