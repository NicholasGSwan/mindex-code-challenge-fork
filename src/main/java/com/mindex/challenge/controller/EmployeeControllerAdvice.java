package com.mindex.challenge.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mindex.challenge.exception.BadCompensationRequestException;
import com.mindex.challenge.exception.CompensationNotFoundException;
import com.mindex.challenge.exception.EmployeeNotFoundException;

@RestControllerAdvice
public class EmployeeControllerAdvice {


    @ExceptionHandler(value = {EmployeeNotFoundException.class, CompensationNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String notFoundHandler(RuntimeException ex){
        return ex.getMessage();
    }

    @ExceptionHandler(value = {BadCompensationRequestException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequestHandler(RuntimeException ex){
        return ex.getMessage();
    }


}
