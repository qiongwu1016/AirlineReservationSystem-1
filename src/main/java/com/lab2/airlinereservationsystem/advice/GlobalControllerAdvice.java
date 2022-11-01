package com.lab2.airlinereservationsystem.advice;

import com.lab2.airlinereservationsystem.common.domain.BadResponse;
import com.lab2.airlinereservationsystem.common.domain.Response;
import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = {ErrorExceptionWrapper.class,Exception.class})
    public BadResponse handlerError(Exception e){
        return BadResponse.fail(400,e.getMessage());
    }

    @ExceptionHandler(value = {ValidExceptionWrapper.class})
    public BadResponse handlerValidError(Exception e){
        return BadResponse.fail(404,e.getMessage());
    }

}
