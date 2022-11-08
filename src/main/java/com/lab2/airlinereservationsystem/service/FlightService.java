package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.FlightDao;
import com.lab2.airlinereservationsystem.dao.PassengerDao;
import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import com.lab2.airlinereservationsystem.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FlightService {
    @Autowired
    private FlightDao flightDao;
    @Autowired
    private PassengerDao passengerDao;

    public Flight findOne(String flightNumber, String departureDate) {
        Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate(flightNumber,DateUtil.getDateDay(departureDate));
        if (flight == null ){
            throw new ValidExceptionWrapper(String.format("Sorry, the requested flight with number %s does not exist",flightNumber));
        }
        List<Passenger> passengers =passengerDao.getPassengerByFlightNumberAndDepartureDate(flightNumber,DateUtil.getDateDay(departureDate));

        flight.setPassengers(BeanUtil.simplePassenger(passengers));

        return flight;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrUpdateFlight(Flight requestFlight) {
        if (requestFlight.getPlane().getCapacity()<0){
            throw new ErrorExceptionWrapper("Capacity can't be negative value!");
        }
        if (!DateUtil.getDateDay(requestFlight.getDepartureDate())
                .equals(DateUtil.getDateDay(requestFlight.getDepartureTime()))){
            throw new ErrorExceptionWrapper("Departure Date and Departure Time must be same!");
        }
        Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate(requestFlight.getFlightNumber(),
                requestFlight.getDepartureDate());
        if (null != flight){
            if (requestFlight.getPlane().getCapacity() < flight.getReservations().size()){
                throw new ErrorExceptionWrapper("New capacity can't be less than the reservations for that flight!");
            }
            int saleSeats = flight.getPlane().getCapacity() - flight.getSeatsLeft();
            if (saleSeats> requestFlight.getPlane().getCapacity()){
                throw new ErrorExceptionWrapper("Capacity can't be negative value!");
            }
            checkReversions(flight,requestFlight);
            requestFlight.setSeatsLeft(requestFlight.getPlane().getCapacity() - saleSeats);
        }
        flightDao.save(requestFlight);
        requestFlight.setPassengers(passengerDao.getPassengerByFlightNumberAndDepartureDate(requestFlight.getFlightNumber(),requestFlight.getDepartureDate()));
    }

    private void checkReversions(Flight flight, Flight requestFlight) {
        List<Flight> flightList = flight.getReservations().stream()
                .map(reservation -> reservation.getPassenger().getReservations().stream()
                        .map(Reservation::getFlights)
                        .collect(Collectors.toList()))
                .collect(Collectors.toList())
                .stream()
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
        flightList.removeIf(e->e.equals(requestFlight));
        flightList.add(requestFlight);
        DateUtil.checkCurrentReservationFlightsTimings(flightList);

    }

    public void delete(String flightNumber, String departureDate) {
        Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate(flightNumber,DateUtil.getDateDay(departureDate));
        if (Objects.isNull(flight)){
            throw new ValidExceptionWrapper("flight number does not exist");
        }
        if (!CollectionUtils.isEmpty(flight.getReservations())){
            throw new ErrorExceptionWrapper("Flight with number " + flightNumber
                    + " has one or more reservation");
        }
        flightDao.deleteByFlightNumberAndDepartureDate(flightNumber,DateUtil.getDateDay(departureDate));
    }
}