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
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author Yikang chen, Qiong Wu
 * Services for Reservation Controller to call
 */
@Service
public class ReservationService {

    private static final String QUERY_FORMAT = "Reservation with number %s does not exist";

    private static final String DELETE_FORMAT = "Reservation with number %s does not exist ";

    @Autowired
    private ReservationDao reservationDao;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private FlightDao flightDao;
    @Autowired
    private PassengerDao passengerDao;

    public Reservation findOne(String number) {
        Reservation reservation =findById(number,QUERY_FORMAT);
        simpleMessage(reservation);
        return reservation;
    }

    private void simpleMessage(Reservation reservation) {
        if (null != reservation.getPassenger()){
            reservation.setPassenger(BeanUtil.simplePassenger(reservation.getPassenger()));
        }
        Passenger passenger = reservation.getPassenger();
        passenger.setReservations(null);
        passenger.setBirthyear(null);
        passenger.setGender(null);
        passenger.setPhone(null);
        reservation.setPassenger(passenger);

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
        Passenger passenger = passengerDao.findById(passengerId).get();
        List<Flight> flightList = new ArrayList<>();
        for (int i = 0; i < flightNumbers.size(); i++) {
            Flight flight =flightDao.findFlightByFlightNumberAndDepartureDate(flightNumbers.get(i),DateUtil.getDateDay(departureDates.get(i)));
            if (null == flight){
                throw new ErrorExceptionWrapper(String.format("flight not exists , flight num = %s , departureDate = %s ",flightNumbers.get(i),departureDates.get(i)));
            }

            flightList.add(flight);
        }
        DateUtil.checkCurrentReservationFlightsTimings(flightList);
//
        checkWithExistingPassengerReservations(passengerId, flightList);
        checkSeats(flightList);

        decreaseFlightSeats(flightList);
        flightList = flightList.stream()
                .sorted(Comparator.comparing(Flight::getDepartureTime))
                .collect(Collectors.toList());
        Reservation reservation = new Reservation(passenger, flightList);
//        passenger.getReservations().add(reservation);
//        flightList.forEach(e->e.setReservations(null));
//        passenger.setReservations(null);
        flightList.forEach(e->e.setPassengers(null));
        int price = 0;
        for(Flight flight : flightList){
            price+=flight.getPrice();
        }
        reservation.setPrice(price);
        reservation.setOrigin(flightList.get(0).getOrigin());
        reservation.setDestination(flightList.get(flightList.size() -1).getDestination());
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
                throw new ErrorExceptionWrapper("Sorry, the requested flight with id " +
                         flight.getFlightNumber() +" is full");
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
        Reservation reservation = findById(number,DELETE_FORMAT);
        List<Flight> flightList = reservation.getFlights();
        for (Flight flight:flightList){
            flight.setSeatsLeft(flight.getSeatsLeft()+1);
        }
        flightDao.saveAll(flightList);
        reservationDao.deleteById(reservation.getReservationNumber());
    }


    public Reservation updateReservation(String number, List<String> flightNumberAddList, List<String> departureDateAddList, List<String> flightNumberRemoveList, List<String> departureDateRemoveList) {
        Reservation reservation = findById(number,QUERY_FORMAT);
        int reservationPrice = reservation.getPrice();
        Integer i = 0;
        List<Flight> reservationFlights = reservation.getFlights();
        //remove flights
        if (flightNumberRemoveList != null) {
            i = 0;
            List<Flight> removeFlightList = new ArrayList<>();
            for (String flightNumber : flightNumberRemoveList) {
                for (Flight flight : reservationFlights) {
                    if (flight.getFlightNumber().equals(flightNumber) && departureDateRemoveList.get(i).equals(DateUtil.getDateDay(flight.getDepartureDate()))) {
                        removeFlightList.add(flight);
                    }
                }
                i = i + 1;
            }
            if (removeFlightList.size() != flightNumberRemoveList.size()) {
                throw new ErrorExceptionWrapper("At least one (flight number, departure date) in remove list not found!");
            }

            if (!removeFlightList.isEmpty()) {
                for (Flight flight : removeFlightList) {
                    reservationPrice -= flight.getPrice();
                    flight.setSeatsLeft(flight.getSeatsLeft() + 1);
                    flightDao.save(flight);
                    reservationFlights.remove(flight);
                }
            }

        }
            //add flights
            if (flightNumberAddList != null) {
                List<Pair<String, String>> flightAddList = IntStream.range(0, Math.min(flightNumberAddList.size(), departureDateAddList.size()))
                        .mapToObj(index -> Pair.of(flightNumberAddList.get(index), departureDateAddList.get(index)))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(reservationFlights) && reservationFlights.stream()
                        .map((Flight flight) -> {
                            return Pair.of(flight.getFlightNumber(), DateUtil.getDateDay(flight.getDepartureDate()));
                        })
                        .collect(Collectors.toList())
                        .removeAll(flightAddList)) {
                    throw new ErrorExceptionWrapper("flight number is already exists in reservation");
                }

                List<Flight> addFlightList = new ArrayList<>();
                i = 0;
                for (String flightNumber : flightNumberAddList) {
                    Flight returnFlight = flightDao.findFlightByFlightNumberAndDepartureDate(flightNumber, DateUtil.getDateDay(departureDateAddList.get(i)));
                    if (returnFlight == null) {
                        throw new ErrorExceptionWrapper(String.format("Sorry, the requested flight %s on %s does not exist", flightNumber, departureDateAddList.get(i)));
                    }
                    addFlightList.add(returnFlight);
                    i = i + 1;
                }


                if (!CollectionUtils.isEmpty(addFlightList)) {
                    for (Flight flight : addFlightList) {
                        reservationPrice += flight.getPrice();
                        flight.setSeatsLeft(flight.getSeatsLeft() - 1);
                        if (flight.getSeatsLeft() < 0) {
                            throw new ErrorExceptionWrapper("The total amount of passengers can not exceed the capacity of the reserved plane.");
                        }
                        flightDao.save(flight);
                        reservationFlights.add(flight);
                    }
                }
            }


        DateUtil.checkCurrentReservationFlightsTimings(reservationFlights);
        reservationFlights = reservationFlights.stream()
                .sorted((a,b) -> a.getDepartureTime().compareTo(b.getDepartureDate()))
                .collect(Collectors.toList());
        reservation.setFlights(reservationFlights);
        reservation.setPrice(reservationPrice);
        reservation.setOrigin(reservationFlights.get(0).getOrigin());
        reservation.setDestination(reservationFlights.get(reservationFlights.size() - 1).getDestination());
        reservation = reservationDao.save(reservation);



        simpleMessage(reservation);
        return reservation;
    }
}
