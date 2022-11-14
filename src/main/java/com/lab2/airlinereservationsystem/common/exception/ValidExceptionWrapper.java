package com.lab2.airlinereservationsystem.common.exception;

import lombok.Getter;

/**
 * @Author Yikang Chen, Qiong Wu
 * The error wrapper class to throw 404 error with message
 */
@Getter
public class ValidExceptionWrapper extends RuntimeException{
    /**
     * @param message Error message to be thrown.
     */
    public ValidExceptionWrapper(String message) {
        super(message);
    }
}
