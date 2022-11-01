package com.lab2.airlinereservationsystem.common.exception;

import lombok.Getter;

@Getter
public class ValidExceptionWrapper extends RuntimeException{

    public ValidExceptionWrapper(String message) {
        super(message);
    }
}
