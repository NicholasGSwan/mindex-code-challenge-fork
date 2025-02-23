package com.mindex.challenge.exception;

public class CompensationNotFoundException extends RuntimeException {

    public CompensationNotFoundException(){
        super("No Compensation found for that employee Id.");
    }

}
