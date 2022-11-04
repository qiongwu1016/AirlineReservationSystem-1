package com.lab2.airlinereservationsystem.advice;

import com.lab2.airlinereservationsystem.common.domain.BadJsonResponse;
import com.lab2.airlinereservationsystem.common.domain.BadRequest;
import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.utils.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(value = {ErrorExceptionWrapper.class,Exception.class})
    public ResponseEntity<?> handlerError(Exception e, HttpServletRequest request){
        boolean xml = Boolean.parseBoolean(request.getParameter("xml"));

        return ResponseUtil.convertResponseEntity(xml? BadRequest.fail(400,e.getMessage()): BadJsonResponse.fail(400,e.getMessage()),xml);

    }

    @ExceptionHandler(value = {ValidExceptionWrapper.class})
    public ResponseEntity<?> handlerValidError(Exception e, HttpServletRequest request){
        boolean xml = Boolean.parseBoolean(request.getParameter("xml"));
        return ResponseUtil.convertResponseEntity(xml? BadRequest.fail(404,e.getMessage()): BadJsonResponse.fail(404,e.getMessage()),xml);
    }

}
