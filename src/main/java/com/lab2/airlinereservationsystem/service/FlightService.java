package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.FlightDao;
import com.lab2.airlinereservationsystem.entity.Flight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Service
public class FlightService {
    @Autowired
    private FlightDao flightDao;


    public Flight findOne(String flightNumber, String departureDate) {
        Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate(flightNumber,departureDate);
        if (flight == null ){
            throw new ValidExceptionWrapper(String.format("Sorry, the requested flight with number %s does not exist",flightNumber));
        }
        return flight;
    }

    public void createOrUpdateFlight(Flight requestFlight) {

    }

    public void delete(String flightNumber, String departureDate) {
        Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate(flightNumber,departureDate);
        if (Objects.isNull(flight)){
            throw new ValidExceptionWrapper("flight number does not exist");
        }
        if (!CollectionUtils.isEmpty(flight.getReservations())){
            throw new ErrorExceptionWrapper("Flight with number " + flightNumber
                    + " has one or more reservation");
        }
        flightDao.deleteByFlightNumberAndDepartureDate(flightNumber,departureDate);
    }
}