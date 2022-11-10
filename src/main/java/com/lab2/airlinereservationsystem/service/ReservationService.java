package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.FlightDao;
import com.lab2.airlinereservationsystem.dao.PassengerDao;
import com.lab2.airlinereservationsystem.dao.ReservationDao;
import com.lab2.airlinereservationsystem.entity.*;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import com.lab2.airlinereservationsystem.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    @Autowired
    private PassengerDao passengerDao;

    public Reservation findOne(String number) {
        Reservation reservation =findById(number,QUERY_FORMAT);
//        simpleMessage(reservation);
        return reservation;
    }

    private void simpleMessage(Reservation reservation) {
        if (null != reservation.getPassenger()){
            reservation.setPassenger(BeanUtil.simplePassenger(reservation.getPassenger()));
        }
        if (!CollectionUtils.isEmpty(reservation.getFlights())){
            List<Flight> flightList = reservation.getFlights();
            flightList.forEach(flight -> {
                flight.setPassengers(null);
                flight.setPlane(null);
                flight.setPrice(null);
                flight.setDescription(null);
                flight.setReservations(null);
                flight.setPassengers(null);
            });
            reservation.setFlights(flightList);
        }
    }

    private Reservation findById(String number, String queryFormat) {
        return reservationDao.findById(number)
                .orElseThrow(() -> new ValidExceptionWrapper(String.format(queryFormat,number)));
    }

    public Reservation createReservation(String passengerId, List<String> flightNumbers, List<String> departureDates) {
        Passenger passenger = passengerService.findOne(passengerId);
        SimplePassenger simplePassenger = new SimplePassenger(passenger.getId(),passenger.getFirstname(), passenger.getLastname());
//        List<SimpleFlight> simpleFlightList = new ArrayList<>();
        List<Flight> flightList = new ArrayList<>();
        for (int i = 0; i < flightNumbers.size(); i++) {
            Flight flight =flightDao.findFlightByFlightNumberAndDepartureDate(flightNumbers.get(i),DateUtil.getDateDay(departureDates.get(i)));
            if (null == flight){
                throw new ErrorExceptionWrapper(String.format("flight not exists , flight num = %s , departureDate = %s ",flightNumbers.get(i),departureDates.get(i)));
            }
            SimpleFlight simpleFlight = new SimpleFlight(flight.getFlightNumber(), flight.getDepartureDate(), flight.getDepartureTime(), flight.getArrivalTime(), flight.getOrigin(), flight.getDestination(), flight.getSeatsLeft());
//            simpleFlightList.add(simpleFlight);
            flightList.add(flight);
        }
//        DateUtil.checkCurrentReservationFlightsTimings(flightList);
//
//        checkWithExistingPassengerReservations(passengerId, flightList);
        checkSeats(flightList);

        decreaseFlightSeats(flightList);
        flightList = flightList.stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
        Reservation reservation = new Reservation(passenger, flightList);
        //flightList.forEach(e->e.setReservations(null));
        //passenger.setReservations(null);
        //flightList.forEach(e->e.setPassengers(null));
        int price = 0;
        for(Flight flight : flightList){
            price+=flight.getPrice();
        }
        reservation.setPrice(price);
        reservation.setOrigin(flightList.get(0).getOrigin());
        reservation.setDestination(flightList.get(flightList.size() -1).getDestination());
        reservation.setFlights(flightList);
        reservationDao.save(reservation);
        simpleMessage(reservation);
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
                throw new ErrorExceptionWrapper("Sorry, the requested flight with id "
                        + flight.getSeatsLeft() +" is full" );
            }
        }
    }

    private void checkWithExistingPassengerReservations(String passengerId, List<Flight> flightList){
        Set<Reservation> reservations= passengerDao.findById(passengerId)
                .orElseThrow(() -> new ValidExceptionWrapper(String.format("Sorry, the requested passenger with ID %s does not exist",passengerId)))
                .getReservations();
        List<Flight> currentPassengerFlights=new ArrayList<>();
        for(Reservation reservation:reservations){
            List<Map<String,Object>> resFlightDtos = reservationDao.findFlightNoAndDate(reservation.getReservationNumber());
            List<Flight> resFlights = new ArrayList<>();
            resFlightDtos.forEach( e->{
                Flight flight = flightDao.findFlightByFlightNumberAndDepartureDate((String) e.get("flightNumber"),(Date) e.get("departureDate"));
                if (null != flight){
                    resFlights.add(flight);
                }
            });
            if (!CollectionUtils.isEmpty(resFlights)){
                currentPassengerFlights.addAll(resFlights);
            }

        }
        for (Flight flight : flightList) {
            for (int j = 0; j < currentPassengerFlights.size(); j++) {
                Date currentFlightDepartureDate = flight.getDepartureTime();
                Date currentFlightArrivalDate = flight.getArrivalTime();
                Date min = currentPassengerFlights.get(j).getDepartureTime();
                Date max = currentPassengerFlights.get(j).getArrivalTime();
                if ((currentFlightArrivalDate.compareTo(min) >= 0
                        && currentFlightArrivalDate.compareTo(max) <= 0)
                        || (currentFlightDepartureDate.compareTo(min) >= 0
                        && currentFlightDepartureDate.compareTo(max) <= 0)) {
                    throw new ErrorExceptionWrapper("Sorry, the time of flights overlaps");
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
        if (CollectionUtils.isEmpty(flightRemoveList)) {
            throw new ErrorExceptionWrapper("flightRemove list cannot be empty,if param exists");
        }
        List<Flight> reservationFlights = reservation.getFlights();
        if (!CollectionUtils.isEmpty(reservationFlights) && reservationFlights.stream()
                .map(Flight::getFlightNumber)
                .collect(Collectors.toList())
                .retainAll(flightAddList)){
            throw new ErrorExceptionWrapper("flight number is already exists in reservation");
        }
        flightAddList.removeIf(flightRemoveList::contains);
        int price = reservation.getPrice();
        for (Flight flight: reservationFlights){
            if (flightRemoveList.contains(flight.getFlightNumber())){
                price-=flight.getPrice();
                //add seat
                flight.setSeatsLeft(flight.getSeatsLeft()+1);
                flightDao.save(flight);
            }
        }
        reservationFlights.removeIf(e->flightRemoveList.contains(e.getFlightNumber()));

        List<Flight> flightList = flightDao.findFlightsByFlightNumberIn(flightAddList);
        if (flightList.size() != flightAddList.size()){
            throw new ErrorExceptionWrapper("flight number not found!");
        }
        if (!CollectionUtils.isEmpty(flightList)){
            for (Flight flight:flightList){
                price+=flight.getPrice();
                flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                if (flight.getSeatsLeft()<0){
                    throw new ErrorExceptionWrapper("The total amount of passengers can not exceed the capacity of the reserved plane.");
                }
                reservationFlights.add(flight);
            }
        }

        DateUtil.checkCurrentReservationFlightsTimings(reservationFlights);
        flightDao.saveAll(flightList);
        reservationFlights = reservationFlights.stream()
                .sorted((a,b) -> a.getDepartureTime().compareTo(b.getDepartureDate()))
                .collect(Collectors.toList());
        reservation.setFlights(reservationFlights);
        reservation.setPrice(price);
        reservation.setOrigin(reservationFlights.get(0).getOrigin());
        reservation.setDestination(reservationFlights.get(reservationFlights.size() - 1).getDestination());
        reservation = reservationDao.save(reservation);
        simpleMessage(reservation);
        return reservation;
    }
}
