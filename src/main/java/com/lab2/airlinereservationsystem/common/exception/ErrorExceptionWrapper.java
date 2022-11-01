package com.lab2.airlinereservationsystem.common.exception;

import lombok.Getter;

@Getter
public class ErrorExceptionWrapper extends RuntimeException{

    public ErrorExceptionWrapper(String message) {
        super(message);
    }
}
