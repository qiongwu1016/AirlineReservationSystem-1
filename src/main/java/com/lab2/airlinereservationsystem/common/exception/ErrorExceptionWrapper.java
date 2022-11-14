package com.lab2.airlinereservationsystem.common.exception;

import lombok.Getter;

/**
 * @Author Yikang Chen, Qiong Wu
 * The error wrapper class to throw 400 error with message
 */

@Getter
public class ErrorExceptionWrapper extends RuntimeException{
    /**
     * @param message Error message to be thrown.
     */
    public ErrorExceptionWrapper(String message) {
        super(message);
    }
}
