package com.lab2.airlinereservationsystem.utils;

import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.entity.Flight;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtil {
    private static final String FORMATTER = "yyyy-MM-dd-hh";
    private static final String DAT_FORMATTER = "yyyy-MM-dd";

    public static Date getDateHour(String date){
        try {
            return new SimpleDateFormat(FORMATTER, Locale.US).parse(date);
        } catch (ParseException e) {
            throw new ValidExceptionWrapper("date convert error:"+date);
        }
    }

    public static Date getDateDay(String date){
        try {
            return new SimpleDateFormat(DAT_FORMATTER, Locale.US).parse(date);
        } catch (ParseException e) {
            throw new ValidExceptionWrapper("date convert error:"+date);
        }
    }

    public static String getDateDay(Date date){
        return new SimpleDateFormat(DAT_FORMATTER).format(date);
    }

    public static String getDateHour(Date date){
        return new SimpleDateFormat(FORMATTER).format(date);
    }

    public static void checkCurrentReservationFlightsTimings(List<Flight> flightList) {
        for(int i=0;i<flightList.size();i++){
            for(int j=i+1;j<flightList.size();j++){
                Date currentFlightDepartureDate=flightList.get(i).getDepartureTime();
                Date currentFlightArrivalDate=flightList.get(i).getArrivalTime();
                Date min=flightList.get(j).getDepartureTime();
                Date max=flightList.get(j).getArrivalTime();
                if((currentFlightArrivalDate.compareTo(min)>=0 && currentFlightArrivalDate.compareTo(max)<=0) || (currentFlightDepartureDate.compareTo(min)>=0 && currentFlightDepartureDate.compareTo(max)<=0)){
                    throw new ValidExceptionWrapper("Sorry, the timings of flights: "
                            +flightList.get(0).getFlightNumber() +" and "+ flightList.get(1).getFlightNumber()+" overlap" );
                }
            }
        }

    }
}
