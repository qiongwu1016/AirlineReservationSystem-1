package com.lab2.airlinereservationsystem.utils;

import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static final String FORMATTER = "yyyy-MM-dd-hh";
    private static final String DAT_FORMATTER = "yyyy-MM-dd";

    public static Date getDateHour(String date){
        try {
            return new SimpleDateFormat(FORMATTER).parse(date);
        } catch (ParseException e) {
            throw new ValidExceptionWrapper("date convert error:"+date);
        }
    }
    public static Date getDateDay(String date){
        try {
            return new SimpleDateFormat(DAT_FORMATTER).parse(date);
        } catch (ParseException e) {
            throw new ValidExceptionWrapper("date convert error:"+date);
        }
    }
}
