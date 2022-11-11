package com.lab2.airlinereservationsystem.service;

import com.lab2.airlinereservationsystem.common.exception.ErrorExceptionWrapper;
import com.lab2.airlinereservationsystem.common.exception.ValidExceptionWrapper;
import com.lab2.airlinereservationsystem.dao.FlightDao;
import com.lab2.airlinereservationsystem.dao.PassengerDao;
import com.lab2.airlinereservationsystem.dao.ReservationDao;
import com.lab2.airlinereservationsystem.entity.Passenger;
import com.lab2.airlinereservationsystem.entity.Reservation;
import com.lab2.airlinereservationsystem.utils.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import com.lab2.airlinereservationsystem.dao.FlightDao;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class PassengerService {

    private static final String QUERY_FORMAT = "Sorry, the requested passenger with ID %s does not exist";

    private static final String DELETE_FORMATTER = "Passenger with id %S does not exist";

    @Autowired
    private PassengerDao passengerDao;

    @Autowired
    private ReservationDao reservationDao;
    @Autowired
    private FlightDao flightDao;

    @Autowired
    private FlightDao flightDao;

    @Transactional(rollbackFor = Exception.class)
    public void insert(Passenger passenger) {
        //...
        if (passengerDao.existsByPhone(passenger.getPhone())){
            throw new ErrorExceptionWrapper("another passenger with the same number already exists.");
        }
        try {
            passengerDao.save(passenger);
        } catch (Exception e) {
            throw new ErrorExceptionWrapper(e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        //check exists
        Passenger passenger = findById(id,DELETE_FORMATTER);
        //reset flight
        //check reservation
        List<Reservation> reservationList = reservationDao.findAll();
        for (Reservation reservation:reservationList){
            if (reservation.getPassenger() != null && reservation.getPassenger().getId().equals(id)){
                reservationDao.delete(reservation);
            }
        }

        passenger.getReservations().forEach(reservation -> {
            reservation.getFlights().forEach(flight -> {
                flight.setSeatsLeft(flight.getSeatsLeft() +1);
                flightDao.save(flight);
            });
        });
        passengerDao.delete(passenger);


    }

    public Passenger findOne(String id) {
        Passenger passenger =findById(id,QUERY_FORMAT);
        return passenger;
    }

    private Passenger findById(String id,String formatter) {
        return passengerDao.findById(id)
                .orElseThrow(() -> new ValidExceptionWrapper(String.format(formatter,id)));
    }

    @Transactional(rollbackFor = Exception.class)
    public Passenger update(Passenger passenger) {
        Passenger originalPassenger = findById(passenger.getId(),QUERY_FORMAT);
        Passenger phonePassenger = passengerDao.findByPhone(passenger.getPhone());
        if (Objects.nonNull(phonePassenger) && !Objects.equals(phonePassenger.getId(), passenger.getId())){
            throw new ValidExceptionWrapper("another passenger with the same number already exists.");
        }
        Set<Reservation> reservations = originalPassenger.getReservations();
        passenger.setReservations(reservations);
        passengerDao.save(passenger);
        return passenger;
    }

}
