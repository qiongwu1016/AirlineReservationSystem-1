package com.lab2.airlinereservationsystem.utils;

import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.util.*;

public class BeanUtil extends BeanUtils {


    public static List<Passenger> simplePassenger(List<Passenger> passengerList){
        if (CollectionUtils.isEmpty(passengerList)){
            return Collections.emptyList();
        }
        List<Passenger> simpleList = new ArrayList<>(passengerList.size());
        passengerList.forEach(passenger -> simpleList.add(simplePassenger(passenger)));
        return simpleList;
    }

    public static Passenger simplePassenger(Passenger passenger){
        Passenger newPassenger = new Passenger();
        copyProperties(passenger,newPassenger);
//        newPassenger.setReservations(null);
        newPassenger.setBirthyear(null);
        newPassenger.setPhone(null);
        newPassenger.setGender(null);
        return newPassenger;

    }

    public static void convertPassengerSimpleForm(Passenger passenger){
        passenger.setReservations(null);
        passenger.setPhone(null);
        passenger.setBirthyear(null);
        passenger.setGender(null);
    }
    public static void convertReservationSimpleForm(Reservation reservation) {
        reservation.setFlights(null);
        reservation.setPrice(null);
        reservation.setPassenger(null);
    }

    public static void convertFlightSimpleForm(Flight flight) {
        flight.setPrice(null);
        flight.setDescription(null);
        flight.setPlane(null);
        flight.setPassengers(null);
    }

}
