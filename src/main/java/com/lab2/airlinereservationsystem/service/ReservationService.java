package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.FlightDao;
import com.lab2.airlinereservationsystem.dao.ReservationDao;
import com.lab2.airlinereservationsystem.entity.Flight;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final String QUERY_FORMAT = "Reservation with number %s does not exist";

    private static final String DELETE_FORMATTER = "Reservation with number %s does not exist ";

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private FlightDao flightDao;

    public Reservation findOne(String number) {
        return findById(number,QUERY_FORMAT);
    }

    private Reservation findById(String number, String queryFormat) {
        return reservationDao.findById(number)
                .orElseThrow(() -> new ValidExceptionWrapper(String.format(queryFormat,number)));
    }

    public Reservation createReservation(String passengerId, List<String> flightNumbers) {
        Passenger passenger = passengerService.findOne(passengerId);
        List<Flight> flightList = flightDao.findFlightsByFlightNumberIn(flightNumbers);
        checkCurrentReservationFlightsTimings(flightList);

        checkWithExistingPassengerReservations(passengerId, flightList);
        checkSeats(flightList);

        decreaseFlightSeats(flightList);
        flightList = flightList.stream()
                .sorted((a,b) -> a.getDepartureTime().compareTo(b.getDepartureDate()))
                .collect(Collectors.toList());
        Reservation reservation = new Reservation(passenger, flightList);
        passenger.getReservations().add(reservation);
        int price = 0;
        for(Flight flight : flightList){
            flight.getPassengers().add(passenger);
            price+=flight.getPrice();
        }
        reservation.setPrice(price);
        reservation.setOrigin(flightList.get(0).getOrigin());
        reservation.setDestination(flightList.get(flightList.size() -1).getDestination());
        reservationDao.save(reservation);
        return reservation;
    }

    private void decreaseFlightSeats(List<Flight> flightList) {
        for(Flight flight : flightList){
            flight.setSeatsLeft(flight.getSeatsLeft()-1);
        }
    }

    private void checkSeats(List<Flight> flightList) {
        for(Flight flight : flightList){
            if(flight.getSeatsLeft() <= 0) {
                throw new ValidExceptionWrapper("Sorry, the requested flight with id "
                        + flight.getSeatsLeft() +" is full" );
            }
        }
    }

    private void checkWithExistingPassengerReservations(String passengerId, List<Flight> flightList){
        List<Reservation> reservations=passengerService.findOne(passengerId).getReservations();
        List<Flight> currentPassengerFlights=new ArrayList<>();
        for(Reservation reservation:reservations){
            currentPassengerFlights.addAll(reservation.getFlights());
        }
        for (Flight flight : flightList) {
            for (int j = 0; j < currentPassengerFlights.size(); j++) {
                Date currentFlightDepartureDate = flight.getDepartureTime();
                Date currentFlightArrivalDate = flight.getArrivalTime();
                Date min = currentPassengerFlights.get(j).getDepartureTime();
                Date max = currentPassengerFlights.get(j).getArrivalTime();
                if ((currentFlightArrivalDate.compareTo(min) >= 0 && currentFlightArrivalDate.compareTo(max) <= 0) || (currentFlightDepartureDate.compareTo(min) >= 0 && currentFlightDepartureDate.compareTo(max) <= 0)) {
                    throw new ValidExceptionWrapper("Sorry, the timings of flights: "
                            + flight.getFlightNumber() + " and " + flightList.get(j).getFlightNumber() + " overlap");
                }
            }
        }
    }

    private void checkCurrentReservationFlightsTimings(List<Flight> flightList) {
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

    public void delete(String number) {
        Reservation reservation = findById(number,DELETE_FORMATTER);
        List<Flight> flightList = reservation.getFlights();
        for (Flight flight:flightList){
            flight.setSeatsLeft(flight.getSeatsLeft()+1);
        }
        flightDao.saveAll(flightList);
        reservationDao.deleteById(reservation.getReservationNumber());
    }

    public Reservation updateReservation(String number, List<String> flightAddList, List<String> flightRemoveList) {
        Reservation reservation = findById(number,QUERY_FORMAT);
        if (CollectionUtils.isEmpty(flightAddList)) {
            throw new ErrorExceptionWrapper("flightsAdded list cannot be empty,if param exists");
        }


        return null;
    }
}
